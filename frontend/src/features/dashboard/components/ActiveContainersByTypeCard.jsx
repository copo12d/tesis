import React, { useMemo } from "react"
import { Box } from "@chakra-ui/react"
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Label } from "recharts"
import { DashboardCard } from "./DashboardCard"

const PALETTE = ["#16a34a", "#22c55e", "#84cc16", "#65a30d", "#a3e635", "#4d7c0f"]

const MOCK = [
  { name: "Plástico", value: 12 },
  { name: "Orgánico", value: 8 },
  { name: "Vidrio", value: 5 },
  { name: "Metal", value: 3 },
]

export function ActiveContainersByTypeCard({
  title = "Contenedores activos",
  data = MOCK,
  height = 140,
}) {
  const series = useMemo(
    () => (data || []).map((d, i) => ({ ...d, fill: d.fill || PALETTE[i % PALETTE.length] })),
    [data]
  )
  const total = useMemo(
    () => series.reduce((sum, d) => sum + (Number(d.value) || 0), 0),
    [series]
  )

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
                          {total}
                        </tspan>
                        <tspan x={cx} y={cy + 16} style={{ fill: "rgba(15,23,42,0.7)", fontSize: "12px" }}>
                          Activos
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
                const pct = total ? ((v / total) * 100).toFixed(1) : 0
                return [`${v} (${pct}%)`, props.payload.name]
              }}
            />
          </PieChart>
        </ResponsiveContainer>
      </Box>
    </DashboardCard>
  )
}