import { Box, ChakraProvider, theme } from '@chakra-ui/react';
import React from 'react';
import { ColorModeSwitcher } from './ColorModeSwitcher';

function App() {
  return (
    <ChakraProvider theme={theme}>
      <Box textAlign="center" fontSize="xl">
        Log generator
        <ColorModeSwitcher />
      </Box>
    </ChakraProvider>
  );
}

export default App;
