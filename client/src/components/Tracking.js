import {
  Box,
  Button,
  Text,
  useBoolean,
  useColorMode,
  VStack,
} from '@chakra-ui/react';
import { Stomp } from '@stomp/stompjs';
import { React, useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import Chart from './Chart';

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

function Tracking({ jobID, setJobID }) {
  const [isLoading, setIsLoading] = useBoolean(false);
  const [isRunning, setRunning] = useBoolean(false);
  const [uptime, setUptime] = useState(0);
  const [data, setData] = useState({
    timeStamp: [],
    logRate: [],
  });
  const [logsCreated, setlogsCreated] = useState(0);
  const lastResponseRef = useRef({ time: 0, response: null });
  const { colorMode } = useColorMode();

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
        response.timeStamp = response.timeStamp * 1000;

        console.log(response);
        setlogsCreated(response.logLineCount);

        if (lastResponseRef.current.response === null) {
          setData({
            timeStamp: [response.timeStamp],
            logRate: [0],
          });
        } else {
          const logRate = Math.round(
            (response.logLineCount -
              lastResponseRef.current.response.logLineCount) /
              ((response.timeStamp -
                lastResponseRef.current.response.timeStamp) /
                1000)
          );

          setData(prev => ({
            timeStamp: [...prev.timeStamp, response.timeStamp],
            logRate: [...prev.logRate, logRate],
          }));
        }

        lastResponseRef.current.response = response;
        lastResponseRef.current.time = Date.now();
      });
    });

    setUptime(0);
    setlogsCreated(0);
    lastResponseRef.current = { time: Date.now(), response: null };

    setRunning.on();

    return () => stompClient.deactivate();
  }, [jobID, setRunning, setData]);

  // uptime and active intervals while job is running
  useEffect(() => {
    let timerInterval;
    let activeInterval;
    if (isRunning) {
      timerInterval = setInterval(() => {
        setUptime(prevTime => prevTime + 1);
      }, 1000);

      activeInterval = setInterval(() => {
        if (Number(Date.now() - lastResponseRef.current.time) > 2500) {
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

  // handle cancel button
  const handleCancel = () => {
    console.log(data);
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
      <Box minW="35em" pt="2em">
        <Chart data={data} colorMode={colorMode} />
      </Box>
    </VStack>
  );
}

export default Tracking;
