import { Center, Spinner, Stack } from "@chakra-ui/react";
import React from "react";
import { useMeQuery } from "../generated/graphql";
import Header from "./Header";

export default function Layout({ children }) {
  const { data, loading } = useMeQuery({ notifyOnNetworkStatusChange: true });

  if (loading) {
    return (
      <Stack
        height="100vh"
        alignItems="center"
      >
        <Center
          flexGrow={1}
        >
          <Spinner />
        </Center>
      </Stack>
    );
  }

  return (
    <Stack
      height="100vh"
      alignItems="center"
    >
      <Header
        maxW="6xl"
        margin="0 auto"
        width="100%"
        user={data?.me}
      />
      {children}
    </Stack>
  );
}