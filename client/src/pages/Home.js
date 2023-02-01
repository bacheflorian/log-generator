import { Box, Flex } from '@chakra-ui/react';
import React from 'react';
import Navbar from '../components/Navbar';
import Settings from '../components/Settings';

function Home() {
  return (
    <div>
      <Navbar />
      <Flex
        align="center"
        justify="space-between"
        p="2em 5%"
        wrap="wrap"
        rowGap="3em"
      >
        <Box maxW="40em" minW="20em" pl="20%">
          <Settings />
        </Box>
      </Flex>
    </div>
  );
}

export default Home;
