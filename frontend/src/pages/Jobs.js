import { Box, Heading } from '@chakra-ui/react';
import { React, useEffect, useState } from 'react';
import BatchTable from '../components/tables/BatchTable';
import StreamTable from '../components/tables/StreamTable';

function Jobs() {
  const [batchJobs, setBatchJobs] = useState(null);
  const [streamJobs, setStreamJobs] = useState(null);

  async function updateJobs() {
    fetch(process.env.REACT_APP_API_URL + 'generate/stats', {
      method: 'GET',
    })
      .then(response => {
        if (response.ok) {
          return response.json();
        }

        throw Error("Couldn't get jobs at this time");
      })
      .then(data => {
        console.log(data);
        console.log(data.batchJobs);
        setBatchJobs(data.batchJobs.sort((a, b) => b.startTime - a.startTime));
        setStreamJobs(
          data.streamJobs.sort((a, b) => b.startTime - a.startTime)
        );
      })
      .catch(err => alert(err));
  }

  useEffect(() => {
    updateJobs();
    return () => {
      //second;
    };
  }, []);

  return (
    <div>
      <Heading as="h4" size="sm" pb="0.75em" ml="10.5%" mt="3em">
        Stream Jobs
      </Heading>
      {batchJobs !== null && (
        <Box m="auto" maxW="75%" pb="1em">
          <StreamTable data={streamJobs.filter(job => job.completed)} />
        </Box>
      )}
      <Heading as="h4" size="sm" pb="0.75em" ml="10.5%" mt="3em">
        Batch Jobs
      </Heading>
      {batchJobs !== null && (
        <Box m="auto" maxW="75%" pb="1em">
          <BatchTable data={batchJobs.filter(job => job.completed)} />
        </Box>
      )}
    </div>
  );
}

export default Jobs;
