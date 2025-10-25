import { Box } from "@chakra-ui/react";
import { DashboardCard } from "./DashboardCard";
import { CircularProgressbar, buildStyles } from "react-circular-progressbar";
import "react-circular-progressbar/dist/styles.css";

export const Carta = ({ value = 0, title = "" }) => {
  const v = Number.isFinite(Number(value)) ? Number(value) : 0;

  return (
    <DashboardCard title={title}>
      <Box mx="auto" mt={2} mb={1} w="110px" h="110px">
        <CircularProgressbar
          value={v}
          maxValue={100}
          text={`${v}%`}
          strokeWidth={12}
          styles={buildStyles({
            textSize: "22px",
            textColor: "#0f172a",
            pathColor: "#5a9e18",
            trailColor: "#d9cc1d",
            pathTransitionDuration: 0.5,
            strokeLinecap: "round",
          })}
        />
      </Box>
    </DashboardCard>
  );
};


