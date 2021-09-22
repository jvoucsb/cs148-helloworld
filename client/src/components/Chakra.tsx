import {
  ChakraProvider,
  cookieStorageManager,
  localStorageManager,
  theme
} from "@chakra-ui/react";

export const Chakra = ({ cookies, children }) => {
  // b) Pass `colorModeManager` prop
  const colorModeManager =
    typeof cookies === "string"
      ? cookieStorageManager(cookies)
      : localStorageManager
  return (
    <ChakraProvider colorModeManager={colorModeManager} theme={theme}>
      {children}
    </ChakraProvider>
  )
}

// also export a reusable function getServerSideProps
export const getServerSideProps = ({ req }) => {
  return {
    props: {
      // first time users will not have any cookies and you may not return
      // undefined here, hence ?? is necessary
      cookies: req.headers.cookie ?? "",
    },
  }
}

export default Chakra;