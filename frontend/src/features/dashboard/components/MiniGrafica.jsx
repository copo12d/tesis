// "use client";

// import { Chart, useChart } from "@chakra-ui/charts";
// import { CartesianGrid, Line, LineChart, Tooltip, XAxis, YAxis } from "recharts";

// export const MiniGrafica = ({
//   data = [
//     { value: 110, day: "01" },
//     { value: 120, day: "02" },
//     { value: 105, day: "03" },
//     { value: 98, day: "04" },
//     { value: 115, day: "05" },
//     { value: 112, day: "06" },
//     { value: 118, day: "07" },
//     { value: 121, day: "08" },
//     { value: 119, day: "09" },
//     { value: 117, day: "10" },
//   ],
//   width = 180,
//   height = 60
// }) => {
//   const chart = useChart({
//     data,
//     series: [{ name: "value", color: "teal.solid" }],
//   });

//   // Solo muestra el primer, el del medio y el último label
//   const showLabel = (index, total) =>
//     index === 0 || index === Math.floor(total / 2) || index === total - 1;

//   return (
//     <Chart.Root chart={chart}>
//       <LineChart
//         data={chart.data}
//         width={width}
//         height={height}
//         margin={{ top: 0, right: 0, left: -40, bottom: 0 }}
//       >
//         <CartesianGrid stroke={chart.color("border")} vertical={false} />
//         <XAxis
//           axisLine={false}
//           dataKey={chart.key("day")}
//           tickFormatter={(_, index) =>
//             showLabel(index, chart.data.length) ? chart.data[index].day : ""
//           }
//           stroke={chart.color("border")}
//           fontSize={11}
//         />
//         <YAxis
//           axisLine={false}
//           tickLine={false}
//           tickMargin={8}
//           stroke={chart.color("border")}
//           fontSize={11}
//         />
//         <Tooltip
//           animationDuration={100}
//           cursor={false}
//           content={<Chart.Tooltip />}
//         />
//         {chart.series.map((item) => (
//           <Line
//             key={item.name}
//             isAnimationActive={false}
//             dataKey={chart.key(item.name)}
//             stroke={chart.color(item.color)}
//             strokeWidth={2}
//             dot={false}
//           />
//         ))}
//       </LineChart>
//     </Chart.Root>
//   );
// };