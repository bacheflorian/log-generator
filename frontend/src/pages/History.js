import { Box, Heading, Text } from '@chakra-ui/react';
import { React } from 'react';
import BatchTable from '../components/tables/BatchTable';
import StreamTable from '../components/tables/StreamTable';
import { useJobs } from '../hooks/useJobs';

function History() {
  const [batchJobs, streamJobs] = useJobs();

  function displayTable(
    Component,
    data,
    emptyMessage = 'No data available currently'
  ) {
    if (data.length > 0) {
      return <Component data={data} />;
    } else {
      return <Text color="gray.500">{emptyMessage}</Text>;
    }
  }

  return (
    <div>
      <Heading as="h4" size="sm" pb="0.75em" ml="10.5%" mt="3em">
        Stream Jobs
      </Heading>
      {streamJobs && (
        <Box m="auto" maxW="75%" pb="1em">
          {displayTable(
            StreamTable,
            streamJobs.filter(job => job.completed),
            'There are no completed stream jobs currently'
          )}
        </Box>
      )}
      <Heading as="h4" size="sm" pb="0.75em" ml="10.5%" mt="3em">
        Batch Jobs
      </Heading>
      {batchJobs && (
        <Box m="auto" maxW="75%" pb="5em">
          {displayTable(
            BatchTable,
            batchJobs.filter(job => job.completed),
            'There are no completed batch jobs currently'
          )}
        </Box>
      )}
    </div>
  );
}

export default History;
