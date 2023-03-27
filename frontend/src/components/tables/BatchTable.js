import { DownloadIcon, NotAllowedIcon } from '@chakra-ui/icons';
import { Center, Link, Tooltip } from '@chakra-ui/react';
import { React, useMemo } from 'react';
import DataTable from './DataTable';

function BatchTable({ data }) {
  const columns = useMemo(
    () => [
      {
        Header: 'Job id',
        accessor: 'jobId',
      },

      {
        Header: 'Generated Logs',
        accessor: 'logCount',
      },
      {
        Header: 'Start Time',
        accessor: d => new Date(d.startTime * 1000).toLocaleString(),
      },
      {
        Header: 'End Time',
        accessor: d => new Date(d.endTime * 1000).toLocaleString(),
      },
      {
        Header: 'Run Time (s)',
        accessor: 'runTime',
      },
      {
        Header: 'Batch Size',
        accessor: 'batchSize',
      },
      {
        Header: 'Status',
        accessor: 'status',
      },
      {
        Header: 'Download',
        accessor: 'batchObjectURL',
        Cell: props =>
          props.value ? (
            <Link
              href={props.value}
              isExternal
              display="flex"
              justifyContent="center"
            >
              <Tooltip label="Download">
                <DownloadIcon mx="2px" />
              </Tooltip>
            </Link>
          ) : (
            <Center>
              <Tooltip label="Not Available">
                <NotAllowedIcon mx="2px" />
              </Tooltip>
            </Center>
          ),
      },
    ],
    []
  );

  return <DataTable columns={columns} data={data} />;
}

export default BatchTable;
