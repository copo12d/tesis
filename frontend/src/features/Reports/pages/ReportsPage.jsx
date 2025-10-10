import {
  Grid,
  GridItem,
  Button,
  Text,
  Box,
  Icon,
} from "@chakra-ui/react";
import { ReportsAPI } from "../api/api.reports";
import { toast } from "react-hot-toast";
import { FiUsers, FiBox } from "react-icons/fi";
import { MdBatchPrediction } from "react-icons/md";
import { useState } from "react";
import ReportDialog from "../components/ReportDialog";
import { useDownloadUsersReport } from "../hooks/useDownloadUsersReport"; // Importa el hook

function downloadFile(response, filename = "reporte.pdf") {
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}

export function ReportsPage() {
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("ASC");
  const { downloadUsersReport, loading: loadingUsers } = useDownloadUsersReport();

  // Define las acciones aquí para pasarlas como onClick
  const handleDownload = async (action, label, params = {}) => {
    try {
      const response = await action(params);
      downloadFile(response, `${label}.pdf`);
      toast.success(`El reporte "${label}" se ha descargado.`);
    } catch {
      toast.error("No se pudo descargar el reporte.");
    }
  };

  const reports = [
    {
      label: "Reporte Batch 1",
      icon: MdBatchPrediction,
      iconColor: "teal.500",
      dialog: false,
      onClick: () => handleDownload(ReportsAPI.downloadBatch1, "Reporte Batch 1"),
    },
    {
      label: "Reporte Batch 2",
      icon: MdBatchPrediction,
      iconColor: "teal.400",
      dialog: false,
      onClick: () => handleDownload(ReportsAPI.downloadBatch2, "Reporte Batch 2"),
    },
    {
      label: "Reporte de Usuarios",
      icon: FiUsers,
      iconColor: "teal.600",
      dialog: false,
      onClick: downloadUsersReport, // Usa el hook aquí
      isLoading: loadingUsers,
    },
    {
      label: "Reporte de Contenedores",
      icon: FiBox,
      iconColor: "teal.700",
      dialog: true,
    },
  ];

  return (
    <Box p={{ base: 4, md: 8 }} bg="white" minH="100vh">
      <Text
        fontSize={{ base: "2xl", md: "3xl" }}
        fontWeight="bold"
        mb={8}
        color="teal.700"
        letterSpacing="wide"
      >
        Reportes
      </Text>
      <Grid templateColumns={{ base: "1fr", md: "repeat(2, 1fr)" }} gap={8}>
        {reports.map((report) => (
          <GridItem key={report.label}>
            {report.dialog ? (
              <ReportDialog
                icon={report.icon}
                iconColor={report.iconColor}
                label={report.label}
                onDownload={({ sortBy, sortDir }) =>
                  handleDownload(
                    ReportsAPI.downloadContainers,
                    "Reporte de Contenedores",
                    { sortBy, sortDir }
                  )
                }
                sortBy={sortBy}
                setSortBy={setSortBy}
                sortDir={sortDir}
                setSortDir={setSortDir}
              />
            ) : (
              <Button
                w="100%"
                h="200px"
                borderRadius="xl"
                boxShadow="md"
                bgGradient="linear(to-r, #c6ea8d, #feffb8, #c6ea8d)"
                display="flex"
                flexDirection="column"
                alignItems="center"
                justifyContent="center"
                transition="box-shadow 0.2s"
                _hover={{ boxShadow: "xl", bg: "teal.100" }}
                p={8}
                onClick={report.onClick}
                loading={report.isLoading}
              >
                <Icon
                  as={report.icon}
                  boxSize={14}
                  color={report.iconColor}
                  mb={3}
                  opacity={0.85}
                />
                <Text
                  fontWeight="bold"
                  fontSize="lg"
                  mb={2}
                  color="teal.800"
                  letterSpacing="wide"
                >
                  {report.label}
                </Text>
                <Text color="teal.700" fontSize="md" fontWeight="medium">
                  Descargar
                </Text>
              </Button>
            )}
          </GridItem>
        ))}
      </Grid>
    </Box>
  );
}