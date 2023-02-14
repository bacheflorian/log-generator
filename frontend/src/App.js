import { ChakraProvider, theme } from '@chakra-ui/react';
import React from 'react';

import Navbar from './components/Navbar';
import Home from './pages/Home';

function App() {
  return (
    <ChakraProvider theme={theme}>
      <Navbar />
      <Home />
    </ChakraProvider>
  );
}

export default App;
