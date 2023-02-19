import { ChakraProvider, theme } from '@chakra-ui/react';
import React from 'react';
import { Route, Routes } from 'react-router-dom';

import Navbar from './components/Navbar';
import ActiveJobs from './pages/ActiveJobs';
import History from './pages/History';
import Home from './pages/Home';

function App() {
  return (
    <ChakraProvider theme={theme}>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/active-jobs" element={<ActiveJobs />} />
        <Route path="/history" element={<History />} />
      </Routes>
    </ChakraProvider>
  );
}

export default App;
