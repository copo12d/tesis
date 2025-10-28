import '../styles/dashboard.css';
import { Carta } from '../components/Carta';
import Grafica_semanal from '../components/Grafica_semanal';
import { useDashboard } from '../hooks/useDashboard';
import { Box, Heading, Grid, GridItem } from '@chakra-ui/react';
import { ActiveContainersByTypeCard } from '../components/ActiveContainersByTypeCard';

const Dashboard = () => {
  const { dashboardData } = useDashboard();

  const card1 = dashboardData?.cardsData?.[0] ?? { value: 0 };
  const card2 = dashboardData?.cardsData?.[1] ?? { value: 0 };
  const card3 = dashboardData?.cardsData?.[2] ?? { value: 0 };

  return (
    <Box as="main" p={{ base: 4, md: 6 }} bg="gray.50" minH="100vh">
      {/* Contenedor centrado para evitar excesivo espacio lateral */}
      <Box maxW="1200px" mx="auto">
        <Heading as="h2" size="lg" color="green.600" mb={6}>
          Centro de Análisis
        </Heading>

        {/* Fila de métricas principales: 12 cols, cada card ocupa 4 */}
        <Grid
          templateColumns={{ base: '1fr', md: 'repeat(12, 1fr)' }}
          gap={6}
          mb={2}
          justifyItems="stretch"
          alignItems="stretch"
        >
          <GridItem colSpan={{ base: 12, md: 4 }}>
            {/* Nueva card con PieChart */}
            <ActiveContainersByTypeCard />
          </GridItem>

          <GridItem colSpan={{ base: 12, md: 4 }}>
            <Box w="100%">
              <Carta value={card2.value} title="Cantidad de usuarios" />
            </Box>
          </GridItem>
          <GridItem colSpan={{ base: 12, md: 4 }}>
            <Box w="100%">
              <Carta value={card3.value} title="Cantidad de desechos" />
            </Box>
          </GridItem>
        </Grid>

        {/* Gráfica semanal */}
        <Grid templateColumns={{ base: '1fr' }} gap={6}>
          <GridItem>
            <Grafica_semanal data={dashboardData?.weeklyData ?? []} />
          </GridItem>
        </Grid>
      </Box>
    </Box>
  );
};

export { Dashboard };
