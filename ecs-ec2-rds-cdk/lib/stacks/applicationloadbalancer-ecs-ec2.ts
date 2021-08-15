import * as cdk from '@aws-cdk/core';
import * as ecs from '@aws-cdk/aws-ecs';
import * as ec2 from '@aws-cdk/aws-ec2';
import * as elb from '@aws-cdk/aws-elasticloadbalancingv2';
import * as path from 'path';
import { CfnOutput } from '@aws-cdk/core';

interface EcsApplicationLoadBalancerStackProps extends cdk.StackProps {
  readonly vpc: ec2.IVpc;
  readonly frontendImage: { directory: string, props?: ecs.AssetImageProps | undefined };
  readonly backendImage: { directory: string, props?: ecs.AssetImageProps | undefined };
  readonly databaseUri: string;
  readonly accessTokenSecret: string;
  readonly refreshTokenSecret: string;
}

export class ApplicationLoadBalancerEcsEc2Stack extends cdk.Stack {
  public service: ecs.Ec2Service;

  constructor(scope: cdk.Construct, id: string, props: EcsApplicationLoadBalancerStackProps) {
    super(scope, id, props);
    
    const loadBalancer = new elb.ApplicationLoadBalancer(scope, "LoadBalancer", {
      vpc: props.vpc,
      internetFacing: true,
    });

    const cluster = new ecs.Cluster(scope, 'EcsCluster', {
      clusterName: 'ecs-on-ec2-cluster',
      vpc: props.vpc,
      capacity: {
        instanceType: ec2.InstanceType.of(ec2.InstanceClass.BURSTABLE2, ec2.InstanceSize.MICRO),
        maxCapacity: 1,
      },
    });

    const taskDefinition = new ecs.Ec2TaskDefinition(scope, 'EcsTaskDefinition');

    taskDefinition.addContainer("FrontendContainer", {
      containerName: "express-react-nextjs-frontend",
      image: ecs.ContainerImage.fromAsset(props.frontendImage.directory, props.frontendImage.props),
      memoryLimitMiB: 256,
      environment: {
        "BASE_API_URL": `http://${loadBalancer.loadBalancerDnsName}:8080`
      },
      portMappings: [
        { containerPort: 3000, hostPort: 80, protocol: ecs.Protocol.TCP }
      ]
    });

    taskDefinition.addContainer("BackendContainer", {
      containerName: "express-react-nextjs-backend",
      image: ecs.ContainerImage.fromAsset(props.backendImage.directory, props.backendImage.props),
      memoryLimitMiB: 256,
      environment: {
        "DATABASE_URL": props.databaseUri,
        "ACCESS_TOKEN_SECRET": props.accessTokenSecret,
        "REFRESH_TOKEN_SECRET": props.refreshTokenSecret,
        "CORS_ORIGIN": `http://${loadBalancer.loadBalancerDnsName}`
      },
      portMappings: [
        { containerPort: 4000, hostPort: 8080, protocol: ecs.Protocol.TCP }
      ]
    });

    this.service = new ecs.Ec2Service(scope, 'EcsOnEc2Service', {
      cluster,
      taskDefinition,
    });

    const frontendTarget = this.service.loadBalancerTarget({
      containerName: "express-react-nextjs-frontend",
      containerPort: 3000,
      protocol: ecs.Protocol.TCP,
    });

    const backendTarget = this.service.loadBalancerTarget({
      containerName: "express-react-nextjs-backend",
      containerPort: 4000,
      protocol: ecs.Protocol.TCP,
    });

    const frontendTargetGroup = new elb.ApplicationTargetGroup(scope, 'FrontendTargetGroup', {
      vpc: props.vpc,
      port: 80,
      targetType: elb.TargetType.INSTANCE,
      protocol: elb.ApplicationProtocol.HTTP
    });

    const backendTargetGroup = new elb.ApplicationTargetGroup(scope, 'BackendTargetGroup', {
      vpc: props.vpc,
      port: 8080,
      targetType: elb.TargetType.INSTANCE,
      protocol: elb.ApplicationProtocol.HTTP
    });

    loadBalancer.addListener("frontend", {
      port: 80,
      protocol: elb.ApplicationProtocol.HTTP,
      defaultTargetGroups: [frontendTargetGroup],
    });

    loadBalancer.addListener("backend", {
      port: 8080,
      protocol: elb.ApplicationProtocol.HTTP,
      defaultTargetGroups: [backendTargetGroup],
    });

    frontendTarget.attachToApplicationTargetGroup(frontendTargetGroup);
    backendTarget.attachToApplicationTargetGroup(backendTargetGroup);

    new CfnOutput(scope, 'LoadBalancerDnsName', { value: loadBalancer.loadBalancerDnsName });
  }
}