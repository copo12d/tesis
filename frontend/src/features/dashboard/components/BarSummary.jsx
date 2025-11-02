import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
 Legend,
} from "recharts";
import { useEffect, useMemo, useState } from "react";
import "../styles/grafica-semanal.css";

const PALETTE = ["#3e87e0", "#ec1414", "#16a34a", "#f59e0b", "#8b5cf6", "#0ea5e9"];

export function BarSummary({
  title = "Comportamiento de contenedores",
  subtitle = "",
  fetch = null,
  fallbackData = [],
  labelKey = "month", // puede ser "week" o "day"
  height = 250,
}) {
  const [data, setData] = useState([]);

  useEffect(() => {
    if (!fetch) {
      setData(fallbackData);
      return;
    }

    fetch()
      .then(res => {
        const raw = res.data?.data;
        if (Array.isArray(raw)) {
          setData(raw);
        } else {
          setData(fallbackData);
        }
      })
      .catch(() => setData(fallbackData));
  }, [fetch, fallbackData]);

  const keys = useMemo(() => {
    if (!data || data.length === 0) return [];
    return Object.keys(data[0]).filter(k => k !== labelKey);
  }, [data, labelKey]);

  const isEmptyData = useMemo(() => {
    return (
      data.length === 0 ||
      data.every(d => Object.keys(d).length === 1 && d[labelKey])
    );
  }, [data, labelKey]);

  return (
    <div className="weekly-chart-container">
      <h3 className="weekly-chart-title" style={{ padding: "12px 16px", color: "#fff" }}>{title}</h3>
      <p className="weekly-chart-subtitle" style={{ padding: "5px 16px", color: "#fff" }}>{subtitle}</p>
      {isEmptyData ? (
        <div style={{ height, display: "flex", alignItems: "center", justifyContent: "center", color: "#ccc" }}>
          <p style={{ fontSize: "16px" }}>No hay datos disponibles</p>
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={height}>
          <BarChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(255, 255, 255, 0.2)" />
            <Tooltip />
            <XAxis
              dataKey={labelKey}
              stroke="rgba(255, 255, 255, 0.8)"
              fontSize={12}
              fontWeight="500"
            />
            <YAxis stroke="rgba(255, 255, 255, 0.8)" fontSize={12} fontWeight="500" />
            <Legend />
            {keys.map((key, index) => (
              <Bar
                key={key}
                dataKey={key}
                name={key.charAt(0).toUpperCase() + key.slice(1)}
                fill={`var(--chart-${index + 1}, ${PALETTE[index % PALETTE.length]})`}
                radius={4}
              />
            ))}
          </BarChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
