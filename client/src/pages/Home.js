import { Box, Flex, Heading } from '@chakra-ui/react';
import React from 'react';
import Settings from '../components/Settings';
import Tracking from '../components/Tracking';

function Home() {
  return (
    <div>
      <Flex
        justify="space-between"
        alignItems="start"
        p="2em 10% 0 20%"
        wrap="wrap"
        rowGap="3em"
      >
        <Box maxW="40em" minW="20em">
          <Heading as="h4" size="sm" ml="-1em" pb="0.5em">
            Options
          </Heading>
          <Settings />
        </Box>
        <Box maxW="50em" minW="25em">
          <Heading as="h4" size="sm" ml="-1em" pb="1.5em">
            Status
          </Heading>
          <Tracking />
        </Box>
      </Flex>
    </div>
  );
}

export default Home;
