import { ApolloProvider } from "@apollo/client";
import { ChakraProvider, ColorModeScript, Stack, theme } from "@chakra-ui/react";
import Header from "../components/Header";
import Layout from "../components/Layout";
import { useApollo } from "../utils/createApollo";

export default function App({ Component, pageProps }) {
  const apolloClient = useApollo(pageProps);

  return (
    <ApolloProvider client={apolloClient}>
      <ChakraProvider theme={theme}>
        <ColorModeScript />
        <Layout>
          <Component {...pageProps} />
        </Layout>
      </ChakraProvider>
    </ApolloProvider>
  );
}
