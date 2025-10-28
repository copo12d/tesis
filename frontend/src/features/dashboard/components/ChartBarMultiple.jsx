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
import "../styles/grafica-semanal.css";

const defaultData = [
  { month: "Enero", desktop: 10, mobile: 8 },
  { month: "Febrero", desktop: 30, mobile: 20 },
  { month: "Marzo", desktop: 23, mobile: 12 },
  { month: "Abril", desktop: 7, mobile: 19 },
  { month: "Mayo", desktop: 20, mobile: 13 },
  { month: "Junio", desktop: 21, mobile: 14 },
  { month: "Julio", desktop: 8, mobile: 9 },
  { month: "Agosto", desktop: 1, mobile: 6 },
  { month: "Septiembre", desktop: 10, mobile: 6 },
  { month: "Octubre", desktop: 3, mobile: 5 },
  { month: "Noviembre", desktop: 21, mobile: 14 },
  { month: "Diciembre", desktop: 21, mobile: 14 },
];

export default function ChartBarMultiple({ data = defaultData, title = "Comportamiento de contenedores", subtitle = "Últimos 6 meses" }) {
  return (
    <div className="weekly-chart-container">
      <h3 className="weekly-chart-title">{title}</h3>
      <p className="weekly-chart-subtitle">{subtitle}</p>
      <ResponsiveContainer width="100%" height={250}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(255, 255, 255, 0.2)" />
          <XAxis
            dataKey="month"
            stroke="rgba(255, 255, 255, 0.8)"
            fontSize={12}
            fontWeight="500"
            tickFormatter={(v) => String(v).slice(0, 3)}
          />
          <YAxis stroke="rgba(255, 255, 255, 0.8)" fontSize={12} fontWeight="500" />
          <Tooltip />
          <Legend />
          <Bar dataKey="desktop" name="Contenedores llenos" fill="var(--chart-1, #3e87e0ff)" radius={4} />
          <Bar dataKey="mobile" name="Contenedores vacíos" fill="var(--chart-2, #ec1414ff)" radius={4} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}