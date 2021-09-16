import { ApolloError } from '@apollo/client';
import { Button, FormControl, FormLabel, Input, useToast } from '@chakra-ui/react';
import { useFormik } from "formik";
import { useRouter } from 'next/router';
import * as React from 'react';
import { MeDocument, MeQuery, useLoginMutation } from '../../generated/graphql';
import { setAccessToken } from '../../utils/accessToken';

const LoginForm: React.FC<{}> = () => {
  const [login] = useLoginMutation();
  const formik = useFormik({
    initialValues: {
      usernameOrEmail: "",
      password: ""
    },
    onSubmit: (values, actions) => {
      actions.setSubmitting(true);
      login({
        variables: {
          input: {
            usernameOrEmail: values.usernameOrEmail,
            password: values.password,
          }
        },
        update: (store, { data }) => {
          if (!data) return null;

          store.writeQuery<MeQuery>({
            query: MeDocument,
            data: {
              me: data.login.user,
            }
          });
        }
      }).then(response => {
        if (!response.data || !response.data.login.token) {
          toast({
            title: "Error",
            description: "An error occured.",
            status: "error",
            duration: 5000,
            isClosable: true,
          });
        } else {
          setAccessToken(response.data.login.token);
          router.push("/profile");
        }
      }).catch((err: ApolloError) => {
        toast({
          title: "Error",
          description: err.message,
          status: "error",
          duration: 5000,
          isClosable: true,
        });
      }).finally(() => actions.setSubmitting(false));
    }
  });
  const toast = useToast();
  const router = useRouter();

  return (
    <form onSubmit={formik.handleSubmit}>
      <FormControl isRequired>
        <FormLabel htmlFor="usernameOrEmail">Username or Email</FormLabel>
        <Input id="usernameOrEmail" {...formik.getFieldProps("usernameOrEmail")} placeholder="Username or Email" />
      </FormControl>
      <FormControl isRequired>
        <FormLabel htmlFor="password">Password</FormLabel>
        <Input id="password" {...formik.getFieldProps("password")} placeholder="Password" type="password" />
      </FormControl>
      <Button mt={6} type="submit" isLoading={formik.isSubmitting}>
        Login
      </Button>
    </form>
  );
};

export default LoginForm;