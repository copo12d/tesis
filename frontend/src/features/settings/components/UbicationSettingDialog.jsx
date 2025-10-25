import {
  Box,
  Button,
  Stack,
  Text,
  Heading,
  VStack,
} from "@chakra-ui/react";
import { useEffect, useState, useRef, useCallback } from "react";
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  useMapEvents,
  useMap,
} from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { toast } from "react-hot-toast";
import { SettingsAPI } from "../api/api.settings";

import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

const MAP_CENTER = [10.6941532, -71.6343502];
const MAP_ZOOM = 20;

function CenterMapButton({ center, zoom }) {
  const map = useMap();
  return (
    <Box position="absolute" top={2} right={2} zIndex={1000}>
      <Button size="sm" onClick={() => map.setView(center, zoom)}>
        Centrar ubicación
      </Button>
    </Box>
  );
}

function LocationSelector({ onClick }) {
  useMapEvents({
    click(e) {
      onClick(e.latlng);
    },
  });
  return null;
}

export default function UbicationSettingDialog() {
  const [form, setForm] = useState({
    latitude: MAP_CENTER[0],
    longitude: MAP_CENTER[1],
    mapZoom: MAP_ZOOM,
  });

  const [loading, setLoading] = useState(false);
  const markerRef = useRef(null);

  useEffect(() => {
    SettingsAPI.getUbication()
      .then((res) => {
        const data = res.data || {};
        setForm({
          latitude: data.latitude || MAP_CENTER[0],
          longitude: data.longitude || MAP_CENTER[1],
          mapZoom: data.mapZoom || MAP_ZOOM,
        });
      })
      .catch((error) => {
        console.error("Error loading ubication data:", error);
        toast.error("Error al cargar los datos de ubicación");
      });
  }, []);

  const handleMapClick = useCallback((latlng) => {
    setForm((prev) => ({
      ...prev,
      latitude: latlng.lat,
      longitude: latlng.lng,
    }));
    setTimeout(() => {
      if (markerRef.current) markerRef.current.openPopup();
    }, 100);
  }, []);

  const handleSubmit = async () => {
    setLoading(true);
    try {
      await SettingsAPI.updateUbication(form);
      toast.success("Ubicación institucional actualizada.");
    } catch {
      toast.error("No se pudo actualizar la ubicación.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack
      spacing={0}
      borderRadius="md"
      boxShadow="md"
      borderWidth={1}
      borderColor="green.600"
      bg="whiteAlpha.900"
      maxW="6xl"
      mx="auto"
    >
      {/* Encabezado verde */}
      <Box
        bg="green.600"
        color="white"
        px={6}
        py={4}
        borderTopRadius="md"
        borderBottom="1px solid"
        borderColor="green.700"
      >
        <Text fontSize="xl" fontWeight="bold">
          Ubicación Institucional
        </Text>
      </Box>

      {/* Contenido del formulario */}
      <Box px={6} py={6}>
        <Stack spacing={4}>
        <VStack align="start" spacing={2} w="full">
          <Text fontSize="sm" fontWeight="medium" color="gray.700">
            Haz clic en el mapa para seleccionar la ubicación
          </Text>
          <Box borderWidth={1} borderRadius="md" overflow="hidden" boxShadow="sm" bg="white">
            <Box width="100%" height="400px" position="relative">
              <MapContainer
                center={[form.latitude || MAP_CENTER[0], form.longitude || MAP_CENTER[1]]}
                zoom={form.mapZoom || MAP_ZOOM}
                scrollWheelZoom={true}
                style={{ width: "100%", height: "100%" }}
              >
                <CenterMapButton center={MAP_CENTER} zoom={MAP_ZOOM} />
                <TileLayer
                  attribution='&copy; OpenStreetMap contributors'
                  url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <LocationSelector onClick={handleMapClick} />
                <Marker
                  position={[form.latitude || MAP_CENTER[0], form.longitude || MAP_CENTER[1]]}
                  draggable={true}
                  ref={markerRef}
                  eventHandlers={{
                    dragend: (e) => {
                      const latlng = e.target.getLatLng();
                      setForm((prev) => ({
                        ...prev,
                        latitude: latlng.lat,
                        longitude: latlng.lng,
                      }));
                      setTimeout(() => {
                        if (markerRef.current) markerRef.current.openPopup();
                      }, 100);
                    },
                  }}
                >
                  <Popup>
                    <Text fontSize="sm">
                      Latitud: {form.latitude?.toFixed(6) || '0.000000'}
                      <br />
                      Longitud: {form.longitude?.toFixed(6) || '0.000000'}
                    </Text>
                  </Popup>
                </Marker>
              </MapContainer>
            </Box>
          </Box>
          {!form.latitude || !form.longitude ? (
            <Text fontSize="xs" color="red.500">
              Debes seleccionar una ubicación en el mapa
            </Text>
          ) : (
            <Text fontSize="xs" color="gray.500">
              Puedes mover el marcador o hacer clic en el mapa para ajustar la ubicación.
            </Text>
          )}
        </VStack>

        <Button
          colorScheme="green"
          bg="green.600"
          color="white"
          _hover={{ bg: "green.700" }}
          _active={{ bg: "green.800" }}
          onClick={handleSubmit}
          isLoading={loading}
          loadingText="Guardando..."
          boxShadow="sm"
        >
          Guardar ubicación
        </Button>
        </Stack>
      </Box>
    </Stack>
  );
}
