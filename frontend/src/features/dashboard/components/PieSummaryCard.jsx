import React, { useEffect, useMemo, useState } from "react";
import { Box } from "@chakra-ui/react";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Label } from "recharts";
import { DashboardCard } from "./DashboardCard";

const PALETTE = ["#16a34a", "#22c55e", "#84cc16", "#65a30d", "#a3e635", "#4d7c0f"];

export function PieSummary({ title, labelText, height = 140, fetch }) {
  const [data, setData] = useState([]);

  useEffect(() => {
    fetch()
      .then(res => {
        const raw = res.data?.data;
        if (Array.isArray(raw)) {
          setData(raw);
        } else if (typeof raw?.processedBatchCount === "number") {
          setData([{ name: labelText, value: raw.processedBatchCount }]);
        } else {
          setData([]);
        }
      })
      .catch(() => setData([]));
  }, [fetch, labelText]);

  const series = useMemo(() => data.map((d, i) => ({ ...d, fill: PALETTE[i % PALETTE.length] })), [data]);
  const total = useMemo(() => series.reduce((sum, d) => sum + (Number(d.value) || 0), 0), [series]);

  return (
    <DashboardCard title={title}>
      <Box h={`${height}px`}>
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={series}
              dataKey="value"
              nameKey="name"
              innerRadius={48}
              outerRadius={68}
              paddingAngle={2}
              stroke="none"
            >
              {series.map((entry, idx) => (
                <Cell key={`cell-${idx}`} fill={entry.fill} />
              ))}
              <Label
                content={({ viewBox }) => {
                  if (viewBox?.cx && viewBox?.cy) {
                    const { cx, cy } = viewBox;
                    return (
                      <text x={cx} y={cy} textAnchor="middle" dominantBaseline="middle">
                        <tspan x={cx} y={cy} style={{ fill: "#ffffffff", fontSize: "22px", fontWeight: 700 }}>
                          {total}
                        </tspan>
                        <tspan x={cx} y={cy + 16} style={{ fill: "rgba(255, 255, 255, 0.7)", fontSize: "12px" }}>
                          {labelText}
                        </tspan>
                      </text>
                    );
                  }
                  return null;
                }}
              />
            </Pie>
            <Tooltip
              formatter={(value, _name, props) => {
                const v = Number(value) || 0;
                const pct = total ? ((v / total) * 100).toFixed(1) : 0;
                return [`${v} (${pct}%)`, props.payload.name];
              }}
            />
          </PieChart>
        </ResponsiveContainer>
      </Box>
    </DashboardCard>
  );
}
