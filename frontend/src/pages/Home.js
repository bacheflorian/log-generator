import { Box, Flex, Heading, Spacer } from '@chakra-ui/react';
import { React, useState } from 'react';
import Settings from '../components/Settings';
import Tracking from '../components/Tracking';

function Home() {
  const [jobID, setJobID] = useState(null);
  const [batchMode, setBatchMode] = useState(false);
  const [batchSize, setBatchSize] = useState(null);

  return (
    <div>
      <Flex alignItems="start" p="2em 7% 0 18%" wrap="wrap" rowGap="3em">
        <Box minW="15em">
          <Heading as="h4" size="sm" ml="-1em" pb="0.5em">
            Options
          </Heading>
          <Settings
            jobID={jobID}
            setJobID={setJobID}
            setBatchMode={setBatchMode}
            setBatchSize={setBatchSize}
          />
        </Box>
        <Spacer />
        <Box maxW="40em" minW="20em" flex="2">
          <Heading as="h4" size="sm" ml="-1em" pb="1.5em">
            Status
          </Heading>
          <Tracking
            jobID={jobID}
            setJobID={setJobID}
            batchMode={batchMode}
            batchSize={batchSize}
          />
        </Box>
      </Flex>
    </div>
  );
}

export default Home;
