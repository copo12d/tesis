import { CircularProgressbar, buildStyles } from "react-circular-progressbar";
import 'react-circular-progressbar/dist/styles.css';
import '../styles/estilos-carta.css';

export const Carta = ({ value }) => {

  return (
    <div className="carta-container">
      <div className="header">
        <div className="title">Día</div>
        <div className="dropdown">
          <button
            className="dropdown-button"
          >
            <span>...</span>
          </button>
          
        <div className="dropdown-content">
            <a href="#">Opción 1</a>
            <a href="#">Opción 2</a>
            <a href="#">Opción 3</a>
        </div>

        </div>
      </div>
      <div className="carta-progress">
        <CircularProgressbar
          value={value}
          maxValue={100}
          text={`${value}%`}
          strokeWidth={12}
          styles={buildStyles({
            textSize: "22px",
            textColor: "#1a1a2e",
            pathColor: "#1a1a2e",
            trailColor: "#fff",
            pathTransitionDuration: 0.5,
            strokeLinecap: "round"
          })}
        />
      </div>
      <div className="carta-title">
        Contenedores activos
      </div>
    </div>
  );
};


