import { CircularProgressbar, buildStyles } from "react-circular-progressbar";
import 'react-circular-progressbar/dist/styles.css';
import '../styles/estilos-carta.css';

// --- Cambios nuevos comentados ---
// import { MiniGrafica } from "./MiniGrafica";

// export const Carta = ({ value, title, graficaData }) => {
//   return (
//     <div className="carta-container">
//       <div className="header">
//         <div className="title" style={{ color: "#fff" }}>{title}</div>
//       </div>
//       <div style={{ margin: "10px 0 0 0", textAlign: "left" }}>
//         <MiniGrafica data={graficaData} />
//       </div>
//       <div
//         className="carta-value"
//         style={{
//           fontSize: "2rem",
//           fontWeight: "bold",
//           color: "#d9cc1d",
//           margin: "0.2rem 0 0 0",
//           textAlign: "left"
//         }}
//       >
//         {value}
//       </div>
//     </div>
//   );
// };

// --- Versión anterior con CircularProgressbar ---
export const Carta = ({ value, title }) => {
  return (
    <div className="carta-container">
      <div className="header">
        <div className="title">{title}</div>
      </div>
      <div className="carta-progress">
        <CircularProgressbar
          value={value}
          maxValue={100}
          text={`${value}%`}
          strokeWidth={12}
          styles={buildStyles({
            textSize: "22px",
            textColor: "#1b1c31",
            pathColor: "#5a9e18",
            trailColor: "#d9cc1d",
            pathTransitionDuration: 0.5,
            strokeLinecap: "round"
          })}
        />
      </div>
    </div>
  );
};


