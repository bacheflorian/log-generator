import { Box, Button, Text, useBoolean, VStack } from '@chakra-ui/react';
import { React, useEffect, useState } from 'react';

function Tracking({ jobID, setJobID }) {
  const [isLoading, setIsLoading] = useBoolean(false);
  const [isRunning, setRunning] = useBoolean(false); //temporary, for demo
  const [uptime, setUptime] = useState(0);
  const [logsCreated, setlogsCreated] = useState(0);

  //conenct to socket once there is a new jobID
  useEffect(() => {
    if (jobID !== null) {
      setUptime(0);
      setRunning.on();
    }
  }, [jobID]);

  // update uptime if server running
  useEffect(() => {
    let interval;
    if (isRunning) {
      interval = setInterval(() => {
        setUptime(prevTime => prevTime + 1);
      }, 1000);
    } else if (!isRunning) {
      clearInterval(interval);
    }
    return () => clearInterval(interval);
  }, [isRunning]);

  // converts seconds to a custom time string
  const secondsToTimeString = totalSeconds => {
    const hours = Math.floor(totalSeconds / 3600);
    totalSeconds %= 3600;
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    let time = '';

    if (Number(hours) > 0) {
      time += hours + 'hr ';
    }
    if (Number(minutes) > 0) {
      time += minutes + 'min ';
    }
    time += seconds + 'sec';

    return time;
  };

  // handle cancel button
  const handleCancel = () => {
    setIsLoading.on();

    fetch(process.env.REACT_APP_API_URL + 'generate/stream/stop', {
      method: 'POST',
    })
      .then(response => {
        if (response.ok) {
          return response.text();
        }

        throw Error("Couldn't cancel at this time, please try again later");
      })
      .then(data => {
        console.log(data);
        setRunning.off();
        setJobID(null);
      })
      .catch(err => alert(err))
      .finally(() => setIsLoading.off());
  };

  return (
    <VStack spacing="0.5em" align="start">
      <Text>{isRunning ? 'Running' : 'Standby'}</Text>
      <Text>Uptime: {secondsToTimeString(uptime)}</Text>logsCreated
      <Text>Logs created: {logsCreated}</Text>
      <Box pt="1em">
        <Button
          type="submit"
          colorScheme="red"
          onClick={handleCancel}
          isLoading={isLoading}
          isDisabled={jobID === null}
        >
          Cancel
        </Button>
      </Box>
    </VStack>
  );
}

export default Tracking;
