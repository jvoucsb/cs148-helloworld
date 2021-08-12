import * as cdk from '@aws-cdk/core';
import * as ecs from '@aws-cdk/aws-ecs';
import * as ecsp from '@aws-cdk/aws-ecs-patterns';
import * as ec2 from '@aws-cdk/aws-ec2';
import * as path from 'path';
import { PostgresRDSStack } from './stacks/postgres-rds-stack';
import { getPostgresUri } from './utils/utils';

export class EcsOnEc2 extends cdk.Stack {

  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const vpc = new ec2.Vpc(this, "Vpc", { maxAzs: 2 });

    const cluster = new ecs.Cluster(this, 'EcsCluster', {
      clusterName: 'ecs-on-ec2-cluster',
      vpc: vpc,
      capacity: {
        instanceType: ec2.InstanceType.of(ec2.InstanceClass.BURSTABLE2, ec2.InstanceSize.MICRO),
        maxCapacity: 1,
      },
    });

    const postgresRDS = new PostgresRDSStack(this, 'postgres-rds', {
      vpc: vpc,
      instanceIdentifier: "express-react-nextjs",
      databaseName: "expressReactNextJs"
    });

    const secret = postgresRDS.databaseInstance.secret!;

    const applicationLoadBalancedService = new ecsp.ApplicationLoadBalancedEc2Service(this, 'EcsOnEc2', {
      cluster,
      memoryLimitMiB: 512,
      taskImageOptions: {
        containerName: "express-react-nextjs-backend",
        image: ecs.ContainerImage.fromAsset(path.join(__dirname, "..", "..", "server")),
        environment: {
          "DATABASE_URL": getPostgresUri(secret)
        },
        containerPort: 4000,
      },
      listenerPort: 80,
      publicLoadBalancer: true
    });

    // grant permissions
    postgresRDS.databaseInstance.connections.allowFrom(applicationLoadBalancedService.service, ec2.Port.tcp(5432), "Allow ec2 to connect to postgres within vpc");
  }
}
