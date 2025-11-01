import React, { useEffect, useState, useMemo } from "react"
import { Box } from "@chakra-ui/react"
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Label } from "recharts"
import { DashboardCard } from "./DashboardCard"
import { DashboardAPI } from "../api/dashboard.api"

const PALETTE = ["#16a34a"]

export function ProcessedBatchSummary({
  title = "Lotes Procesados",
  height = 140,
}) {
  const [count, setCount] = useState(0)

  useEffect(() => {
    DashboardAPI.ProcessedBatchSummary()
      .then(res => setCount(res.data?.data?.processedBatchCount ?? 0))
      .catch(() => setCount(0))
  }, [])

  const series = useMemo(() => {
    return count > 0
      ? [{ name: "Procesados", value: count, fill: PALETTE[0] }]
      : []
  }, [count])

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
                  if (viewBox && "cx" in viewBox && "cy" in viewBox) {
                    const cx = viewBox.cx
                    const cy = viewBox.cy
                    return (
                      <text x={cx} y={cy} textAnchor="middle" dominantBaseline="middle">
                        <tspan x={cx} y={cy} style={{ fill: "#0f172a", fontSize: "22px", fontWeight: 700 }}>
                          {count}
                        </tspan>
                        <tspan x={cx} y={cy + 16} style={{ fill: "rgba(15,23,42,0.7)", fontSize: "12px" }}>
                          Procesados
                        </tspan>
                      </text>
                    )
                  }
                  return null
                }}
              />
            </Pie>
            <Tooltip
              formatter={(value, _name, props) => {
                const v = Number(value) || 0
                return [`${v}`, props.payload.name]
              }}
            />
          </PieChart>
        </ResponsiveContainer>
      </Box>
    </DashboardCard>
  )
}
