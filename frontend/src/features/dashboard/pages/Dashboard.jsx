import '../styles/dashboard.css';
import { Box, Heading, Grid, GridItem, Skeleton } from '@chakra-ui/react';
import { lazy, Suspense } from 'react';
import { DashboardAPI } from '../api/dashboard.api';

const PieSummary = lazy(() => import('../components/PieSummaryCard').then(m => ({ default: m.PieSummary })));
const BarSummary = lazy(() => import('../components/BarSummary').then(m => ({ default: m.BarSummary })));

const Dashboard = () => {

  return (
    <Box as="main" p={{ base: 4, md: 6 }} bg="gray.50" h="100vh" overflowY="auto">
      <Box maxW="1200px" mx="auto">
        <Heading as="h2" size="lg" color="green.600" mb={6} fontSize={25} fontWeight="bold">
          Centro de Análisis
        </Heading>

        <Grid
          templateColumns={{ base: '1fr', md: 'repeat(12, 1fr)' }}
          gap={6}
          mb={2}
          justifyItems="stretch"
          alignItems="stretch"
        >
          <GridItem colSpan={{ base: 12, md: 4 }}>
            <Suspense fallback={<Skeleton h="220px" borderRadius="md" />}>
              <PieSummary
                title="Contenedores activos"
                labelText="Activos"
                fetch={DashboardAPI.getActiveContainerSummary}
              />
            </Suspense>
          </GridItem>

          <GridItem colSpan={{ base: 12, md: 4 }}>
            <Suspense fallback={<Skeleton h="220px" borderRadius="md" />}>
              <PieSummary
                title="Contenedores llenos"
                labelText="Llenos"
                fetch={DashboardAPI.ContainerTypeSummary}
              />
            </Suspense>
          </GridItem>

          <GridItem colSpan={{ base: 12, md: 4 }}>
            <Suspense fallback={<Skeleton h="220px" borderRadius="md" />}>
              <PieSummary
                title="Lotes procesados"
                labelText="Procesados"
                fetch={DashboardAPI.ProcessedBatchSummary}
              />
            </Suspense>
          </GridItem>
        </Grid>

        <Grid templateColumns={{ base: '1fr' }} gap={6}>
          <GridItem>
            <Suspense fallback={<Skeleton h="340px" borderRadius="md" />}>
              <BarSummary
                title="Resumen diario de contenedores"
                subtitle="Semana actual"
                fetch={DashboardAPI.getDailyContainerSummary}
                fallbackData={[
                  { day: "Lunes", papel: 5, vidrio: 3, plastico: 7 },
                  { day: "Martes", papel: 6, vidrio: 4, plastico: 6 },
                  { day: "Miércoles", papel: 4, vidrio: 5, plastico: 8 },
                  { day: "Jueves", papel: 7, vidrio: 2, plastico: 5 },
                  { day: "Viernes", papel: 6, vidrio: 3, plastico: 9 },
                  { day: "Sábado", papel: 8, vidrio: 6, plastico: 4 },
                  { day: "Domingo", papel: 3, vidrio: 2, plastico: 6 },
                ]}
                labelKey="day"
              />
            </Suspense>
          </GridItem>

          <GridItem>
            <Suspense fallback={<Skeleton h="340px" borderRadius="md" />}>
              <BarSummary
                title="Resumen semanal de contenedores"
                subtitle="Mes actual"
                fetch={DashboardAPI.getWeeklyContainerSummary}
                fallbackData={[
                  { week: "Semana 1", papel: 12, vidrio: 8, plastico: 15 },
                  { week: "Semana 2", papel: 10, vidrio: 6, plastico: 12 },
                  { week: "Semana 3", papel: 14, vidrio: 9, plastico: 18 },
                  { week: "Semana 4", papel: 11, vidrio: 7, plastico: 13 },
                ]}
                labelKey="week"
              />
            </Suspense>
          </GridItem>
        </Grid>
      </Box>
    </Box>
  );
};

export { Dashboard };
