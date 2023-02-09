import { Box, Button, Text, useBoolean, VStack } from '@chakra-ui/react';
import { Stomp } from '@stomp/stompjs';
import { React, useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';

function Tracking({ jobID, setJobID }) {
  const [isLoading, setIsLoading] = useBoolean(false);
  const [isRunning, setRunning] = useBoolean(false); //temporary, for demo
  const [uptime, setUptime] = useState(0);
  const [logsCreated, setlogsCreated] = useState(0);
  const lastResponseRef = useRef(null);

  useEffect(() => {}, []);

  //conenct to socket once there is a new jobID
  useEffect(() => {
    if (jobID === null) {
      return;
    }

    let stompClient = Stomp.over(
      () => new SockJS('http://localhost:8080/websocket-batch-service')
    );
    stompClient.debug = () => {}; //disables stomp debug console logs
    stompClient.connect({}, function (frame) {
      stompClient.subscribe('/topic/job/' + jobID, function (response) {
        response = JSON.parse(response.body);
        console.log(response);
        setlogsCreated(response.logLineCount);
        lastResponseRef.current = Date.now();
      });
    });

    setUptime(0);
    setlogsCreated(0);
    lastResponseRef.current = Date.now();
    setRunning.on();

    return () => stompClient.deactivate();
  }, [jobID, setRunning]);

  // uptime and active intervals while job is running
  useEffect(() => {
    let timerInterval;
    let activeInterval;
    if (isRunning) {
      timerInterval = setInterval(() => {
        setUptime(prevTime => prevTime + 1);
      }, 1000);

      activeInterval = setInterval(() => {
        if (Number(Date.now() - lastResponseRef.current) > 2500) {
          setRunning.off();
          setJobID(null);
        }
      }, 1000);
    } else if (!isRunning) {
      clearInterval(timerInterval);
      clearInterval(activeInterval);
    }
    return () => {
      clearInterval(timerInterval);
      clearInterval(activeInterval);
    };
  }, [isRunning, setRunning, setJobID]);

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

    fetch(process.env.REACT_APP_API_URL + 'generate/stream/stop/' + jobID, {
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
