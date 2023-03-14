import React, { useState } from 'react';
import Tracking from './Tracking';

function ActiveJob({ job, batchMode }) {
  const [jobID, setJobID] = useState(job.jobId);

  return (
    <>
      <Tracking
        jobID={jobID}
        setJobID={setJobID}
        startTime={job.startTime * 1000}
        batchMode={batchMode}
        batchSize={job.batchSize}
      />
    </>
  );
}

export default ActiveJob;
