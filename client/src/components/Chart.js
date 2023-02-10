import React from 'react';
import Chart from 'react-apexcharts';

function chart({ data, colorMode }) {
  return (
    <div>
      {data.logRate.length > 1 && (
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
                format: 'HH:m:s',
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
      )}
    </div>
  );
}

export default chart;
