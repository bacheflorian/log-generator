import { Box, Button, Text, useBoolean, VStack } from '@chakra-ui/react';
import { React, useEffect, useState } from 'react';

function Tracking() {
  const [isLoading, setIsLoading] = useBoolean(false);
  const [isRunning, setRunning] = useBoolean(true); //temporary, for demo
  const [uptime, setUptime] = useState(0);
  const [logsCreated, setlogsCreated] = useState(0);

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

    console.log('cancel'); //temporary, for demo

    setTimeout(() => {
      setRunning.off();
      setIsLoading.off(); //temporary, for demo
    }, 500);
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
        >
          Cancel
        </Button>
      </Box>
    </VStack>
  );
}

export default Tracking;
