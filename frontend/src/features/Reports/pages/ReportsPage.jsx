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
import { useDownloadUsersReport } from "../hooks/useDownloadUsersReport";
import { useDownloadContainersReport } from "../hooks/useDownloadContainersReport";
import { useDownloadBatchDetailsReport } from "../hooks/useDownloadBatchDetailsReport";
import { useDownloadBatchesReport } from "../hooks/useDownloadBatchesReport";
import { useProcessBatchDropdown } from "../hooks/useProcessBatchDropdown";
import ReportDialog from "../components/ReportDialog";

function downloadFile(response, filename = "reporte.xlsx") {
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
  const { downloadContainersReport, loading: loadingContainers } = useDownloadContainersReport();

  // Detalle de lote (combo)
  const { downloadBatchDetails, loading: loadingBatchDetails } = useDownloadBatchDetailsReport();
  const [batchId, setBatchId] = useState("");
  const { batches, loadingBatches } = useProcessBatchDropdown();

  const batchOptions = (batches || []).map((b) => ({
    value: b.id,
    label: b.description || `Lote #${b.id}`,
  }));

  // Todos los lotes (rango de fechas opcional)
  const {
    startDate,
    setStartDate,
    endDate,
    setEndDate,
    loading: loadingAllBatches,
    downloadBatchesReport,
  } = useDownloadBatchesReport();

  const handleDownloadContainers = async ({ sortBy, sortDir }) => {
    try {
      const response = await downloadContainersReport({ sortBy, sortDir });
      downloadFile(response, `reporte-contenedores.pdf`);
      toast.success("El reporte de contenedores se ha descargado.");
    } catch {
      toast.error("No se pudo descargar el reporte.");
    }
  };

  const handleDownloadUsers = async ({ sortBy, sortDir }) => {
    try {
      const response = await downloadUsersReport({ sortBy, sortDir });
      downloadFile(response, "reporte_usuarios.pdf");
      toast.success("El reporte de usuarios se ha descargado.");
    } catch {
      // manejado por el hook
    }
  };

  const handleDownloadBatchDetails = async ({ batchId }) => {
    const id = Number(batchId);
    const response = await downloadBatchDetails({ batchId: id });
    downloadFile(response, `detalle-lote-${id}.pdf`);
    toast.success("El detalle del lote se ha descargado.");
  };

  const handleDownloadAllBatches = async ({ startDate, endDate }) => {
    try {
      const res = await downloadBatchesReport({ startDate, endDate });
      const suffix =
        (startDate ? `_${startDate}` : "") + (endDate ? `_${endDate}` : "");
      downloadFile(res, `reporte-lotes${suffix || "_todos"}.pdf`);
      toast.success("El reporte de lotes se ha descargado.");
    } catch {
      toast.error("No se pudo descargar el reporte de lotes.");
    }
  };

  const reports = [
    {
      label: "Reporte de lotes (todos)",
      icon: MdBatchPrediction,
      iconColor: "teal.500",
      dialog: true,
      onDownload: handleDownloadAllBatches,
      loading: loadingAllBatches,
      withDateRange: true,
      startDate,
      setStartDate,
      endDate,
      setEndDate,
      // sin ordenamiento
      sortByOptions: [],
      sortDirOptions: [],
      withSorting: false,
    },
    {
      label: "Detalle de Lote",
      icon: MdBatchPrediction,
      iconColor: "teal.400",
      dialog: true,
      onDownload: handleDownloadBatchDetails,
      loading: loadingBatchDetails || loadingBatches,
      withBatchSelect: true,
      batchId,
      setBatchId,
      batchOptions,
      sortByOptions: [],
      sortDirOptions: [],
      withSorting: false,
    },
    {
      label: "Reporte de Usuarios",
      icon: FiUsers,
      iconColor: "teal.600",
      dialog: true,
      onDownload: handleDownloadUsers,
      loading: loadingUsers,
      sortByOptions: [
        { label: "ID", value: "id" },
        { label: "Usuario", value: "user" },
      ],
      sortDirOptions: [
        { label: "Ascendente", value: "ASC" },
        { label: "Descendente", value: "DESC" },
      ],
    },
    {
      label: "Reporte de Contenedores",
      icon: FiBox,
      iconColor: "teal.700",
      dialog: true,
      onDownload: handleDownloadContainers,
      loading: loadingContainers,
      sortByOptions: [
        { label: "Serial", value: "serial" },
        { label: "Nombre", value: "name" },
      ],
      sortDirOptions: [
        { label: "Ascendente", value: "ASC" },
        { label: "Descendente", value: "DESC" },
      ],
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
                onDownload={report.onDownload}
                loading={report.loading}
                sortBy={sortBy}
                setSortBy={setSortBy}
                sortDir={sortDir}
                setSortDir={setSortDir}
                sortByOptions={report.sortByOptions}
                sortDirOptions={report.sortDirOptions}
                withBatchSelect={report.withBatchSelect}
                batchId={report.batchId}
                setBatchId={report.setBatchId}
                batchOptions={report.batchOptions}
                withDateRange={report.withDateRange}
                startDate={report.startDate}
                setStartDate={report.setStartDate}
                endDate={report.endDate}
                setEndDate={report.setEndDate}
                withSorting={report.withSorting}
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