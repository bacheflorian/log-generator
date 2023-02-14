import { Box, Button, Text, useBoolean, VStack } from '@chakra-ui/react';
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
  const [loading, setLoading] = useBoolean(false);
  const [running, setRunning] = useBoolean(false);
  const [uptime, setUptime] = useState(0);
  const [data, setData] = useState({
    timeStamp: [],
    logRate: [],
  });
  const [logsCreated, setlogsCreated] = useState(0);
  const lastResponseRef = useRef({ time: 0, response: null });
  const [showChart, setShowChart] = useBoolean(false);

  //conenct to socket once there is a new jobID
  useEffect(() => {
    if (jobID === null) {
      return;
    }

    // reset values on new jobID
    setUptime(0);
    setlogsCreated(0);
    lastResponseRef.current = { time: Date.now(), response: null };
    setData({
      timeStamp: [],
      logRate: [],
    });

    // set running on
    setRunning.on();

    // connect to socket
    let stompClient = Stomp.over(
      () => new SockJS('http://localhost:8080/websocket-batch-service')
    );
    stompClient.debug = () => {}; //disables stomp debug console logs
    stompClient.connect({}, function (frame) {
      stompClient.subscribe('/topic/job/' + jobID, function (response) {
        // parse response
        response = JSON.parse(response.body);

        // update logsCreated
        console.log(response);
        setlogsCreated(response.logLineCount);

        // update chart data
        if (lastResponseRef.current.response === null) {
          // set initial data
          if (response.logLineCount === 0) {
            // if no logs generated yet, initial log rate of 0
            setData({
              timeStamp: [response.timeStamp],
              logRate: [0],
            });
          } else {
            // if some logs generated, initial log rate of 0, then number generated
            setData({
              timeStamp: [response.timeStamp - 1000, response.timeStamp],
              logRate: [0, response.logLineCount],
            });
          }

          // show chart once first data recieved
          setShowChart.on();
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

        // update last response ref
        lastResponseRef.current.response = response;
        lastResponseRef.current.time = Date.now();
      });
    });

    // cleanup
    return () => {
      // deactivate socket
      stompClient.deactivate();
    };
  }, [jobID, setRunning, setData, setShowChart]);

  // uptime and active intervals while job is running
  useEffect(() => {
    let timerInterval;
    let activeInterval;
    if (running) {
      // interval to update timer
      timerInterval = setInterval(() => {
        setUptime(prevTime => prevTime + 1);
      }, 1000);

      // interval to timeout socket
      activeInterval = setInterval(() => {
        if (Number(Date.now() - lastResponseRef.current.time) > 2500) {
          setRunning.off();
          setJobID(null);
        }
      }, 1000);
    } else if (!running) {
      clearInterval(timerInterval);
      clearInterval(activeInterval);
    }

    // cleanup
    return () => {
      clearInterval(timerInterval);
      clearInterval(activeInterval);
    };
  }, [running, setRunning, setJobID]);

  // handle cancel button
  const handleCancel = () => {
    setLoading.on();

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
      .finally(() => setLoading.off());
  };

  return (
    <VStack spacing="0.5em" align="start">
      <Text>{running ? 'Running' : 'Standby'}</Text>
      <Text>Uptime: {secondsToTimeString(uptime)}</Text>logsCreated
      <Text>Logs created: {logsCreated}</Text>
      <Box pt="1em">
        <Button
          type="submit"
          colorScheme="red"
          onClick={handleCancel}
          isLoading={loading}
          isDisabled={jobID === null}
        >
          Cancel
        </Button>
      </Box>
      {showChart && (
        <Box w="100%" pt="2em">
          <Chart data={data} />
        </Box>
      )}
    </VStack>
  );
}

export default Tracking;
