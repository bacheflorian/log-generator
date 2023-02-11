import React from 'react';
import Chart from 'react-apexcharts';

function chart({ data, colorMode }) {
  return (
    <Chart
      type="area"
      options={{
        chart: {
          animations: {
            enabled: false,
            easing: 'linear',
            dynamicAnimation: {
              speed: 500,
            },
          },
          toolbar: {
            show: false,
          },
          zoom: {
            enabled: false,
          },
          background: 'transparent',
        },
        theme: {
          mode: colorMode,
        },
        dataLabels: {
          enabled: false,
        },
        stroke: {
          curve: 'smooth',
        },
        tooltip: {
          x: {
            format: 'dd MMM HH:mm:ss',
          },
        },
        xaxis: {
          categories: data.timeStamp,
          type: 'datetime',
          labels: {
            datetimeUTC: false,
            format: 'HH:m:ss',
          },
        },
      }}
      series={[
        {
          name: 'Logs/s',
          data: data.logRate,
        },
      ]}
    />
  );
}

export default chart;
