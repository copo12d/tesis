import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from "recharts";
import "../styles/grafica-semanal.css";
import { CustomTooltip } from "./CustomTooltip";

const Grafica_semanal = ({ data }) => {
  return (
    <div className="weekly-chart-container">
      <h3 className="weekly-chart-title">Esta Semana</h3>
      <p className="weekly-chart-subtitle">Volumen de contenedores</p>
      <ResponsiveContainer width="100%" height={250}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(255, 255, 255, 0.2)" />
          <XAxis 
            dataKey="day" 
            stroke="rgba(255, 255, 255, 0.8)"
            fontSize={12}
            fontWeight="500"
          />
          <YAxis 
            stroke="rgba(255, 255, 255, 0.8)"
            fontSize={12}
            fontWeight="500"
          />
          <Tooltip content={<CustomTooltip />} />
          <Bar 
            dataKey="value" 
            fill="rgba(255, 255, 255, 0.9)" 
            radius={[8, 8, 0, 0]}
            stroke="rgba(255, 255, 255, 1)"
            strokeWidth={1}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default Grafica_semanal;
