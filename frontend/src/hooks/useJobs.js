import { useEffect, useState } from 'react';

export function useJobs() {
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
        setBatchJobs(data.batchJobs.sort((a, b) => b.startTime - a.startTime));
        setStreamJobs(
          data.streamJobs.sort((a, b) => b.startTime - a.startTime)
        );
      })
      .catch(err => console.error(err));
  }

  useEffect(() => {
    updateJobs();
  }, []);

  return [batchJobs, streamJobs];
}
