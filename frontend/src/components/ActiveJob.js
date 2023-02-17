import React, { useState } from 'react';
import Tracking from './Tracking';

function ActiveJob({ job }) {
  const [jobID, setJobID] = useState(job.jobId);

  return (
    <>
      <Tracking
        jobID={jobID}
        setJobID={setJobID}
        startTime={job.startTime * 1000}
        batchSize={job.batchSize}
      />
    </>
  );
}

export default ActiveJob;
