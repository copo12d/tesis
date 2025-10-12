import {  Stack,  Button,  Text,  Field,  Box,} from "@chakra-ui/react";
import {  LiaBarcodeSolid,  LiaRulerCombinedSolid,} from "react-icons/lia";
import { useContainerForm } from "../hooks/useContainerForm";
import { useContainerTypes } from "../hooks/useContainerTypes";
import { useState, useCallback, useRef } from "react";
import {  MapContainer,  TileLayer,  Marker,  useMapEvents,  useMap,  Popup,} from "react-leaflet";
import L from "leaflet";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import { IconInputField } from "@/components/ui/IconInputField";
import { StyledSelectField } from "@/components/ui/StyledSelectField";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

export const MAP_CENTER = [10.6941532, -71.6343502];
export const MAP_ZOOM = 20;

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

export function ContainerForm({
  loading = false,
  initialValues = {},
  onSubmit,
  submitText = "Guardar",
  title,
}) {
  const { form, errors, setField, handleSubmit } = useContainerForm({
    initialValues,
    onSubmit,
  });

  const busy = loading;
  const isEdit = !!initialValues?.id;

  const { types: containerTypes, loading: loadingTypes } = useContainerTypes();

  const [markerPosition, setMarkerPosition] = useState(
    form.latitude && form.longitude
      ? [Number(form.latitude), Number(form.longitude)]
      : null
  );

  const markerRef = useRef(null);

  const onMapClick = useCallback(
    (latlng) => {
      setMarkerPosition([latlng.lat, latlng.lng]);
      setField("latitude", latlng.lat);
      setField("longitude", latlng.lng);

      setTimeout(() => {
        if (markerRef.current) {
          markerRef.current.openPopup();
        }
      }, 100);
    },
    [setField]
  );

  function LocationSelector() {
    useMapEvents({
      click(e) {
        onMapClick(e.latlng);
      },
    });
    return null;
  }

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
      mt={6}
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
          {initialValues?.id ? "Editar contenedor" : "Nuevo contenedor"}
        </Text>
      </Box>

      {/* Contenido del formulario */}
      <Box px={6} py={6}>
        <form onSubmit={handleSubmit}>
          <Stack spacing={6}>
            {/* Tipo de contenedor */}
            <StyledSelectField
              label="Tipo de contenedor"
              name="containerTypeId"
              value={form.containerTypeId ?? ""}
              onChange={(e) => setField("containerTypeId", e.target.value)}
              options={containerTypes.map((type) => ({
                value: type.id,
                label: type.name,
              }))}
              required
              error={errors.containerTypeId}
              disabled={busy || loadingTypes}
              placeholder="Seleccione un tipo de contenedor"
            />

            {/* Serial */}
            <IconInputField
              label="Serial"
              name="serial"
              value={form.serial}
              onChange={(e) => setField("serial", e.target.value)}
              placeholder="Serial del contenedor"
              icon={<LiaBarcodeSolid />}
              iconProps={{ bg: "teal.700", px: 3 }}
              required
              disabled={busy}
              error={errors.serial}
              inputProps={{
                w: "100%",
                pl: 2,
                _placeholder: { pl: 2 },
              }}
            />

            {/* Capacidad */}
            <IconInputField
              label="Capacidad (L)"
              name="capacity"
              value={form.capacity}
              onChange={(e) => setField("capacity", e.target.value)}
              placeholder="Capacidad en litros"
              icon={<LiaRulerCombinedSolid />}
              iconProps={{ bg: "teal.700", px: 3 }}
              required
              disabled={busy}
              error={errors.capacity}
              inputProps={{
                type: "number",
                w: "100%",
                pl: 2,
                _placeholder: { pl: 2 },
              }}
            />

            {/* Ubicación - MAPA */}
            <Field.Root required invalid={!!(errors.latitude || errors.longitude)}>
              <Field.Label color="black" mx="auto">
                Ubicación (haz clic en el mapa para seleccionar)
              </Field.Label>
              <Box
                borderWidth={1}
                borderRadius="md"
                overflow="hidden"
                boxShadow="sm"
                bg="white"
                width="80%"
                mx="auto"
              >
                <Box width="100%" height="400px">
                  <MapContainer
                    center={markerPosition ?? MAP_CENTER}
                    zoom={MAP_ZOOM}
                    scrollWheelZoom={false}
                    style={{
                      width: "100%",
                      height: "100%",
                      position: "relative",
                    }}
                  >
                    <CenterMapButton center={MAP_CENTER} zoom={MAP_ZOOM} />
                    <TileLayer
                      attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />
                    <LocationSelector />
                    {markerPosition && (
                      <Marker
                        position={markerPosition}
                        draggable={true}
                        ref={markerRef}
                        eventHandlers={{
                          dragend: (e) => {
                            const latlng = e.target.getLatLng();
                            setMarkerPosition([latlng.lat, latlng.lng]);
                            setField("latitude", latlng.lat);
                            setField("longitude", latlng.lng);

                            setTimeout(() => {
                              if (markerRef.current) {
                                markerRef.current.openPopup();
                              }
                            }, 100);
                          },
                        }}
                      >
                        <Popup
                          autoClose={false}
                          closeOnClick={false}
                          closeButton={false}
                          keepInView={true}
                        >
                          <Text fontSize="sm">
                            Latitud: {markerPosition[0].toFixed(6)}<br />
                            Longitud: {markerPosition[1].toFixed(6)}
                          </Text>
                        </Popup>
                      </Marker>
                    )}
                  </MapContainer>
                </Box>
              </Box>

              {(errors.latitude || errors.longitude) && (
                <Field.ErrorText>
                  {errors.latitude || errors.longitude}
                </Field.ErrorText>
              )}
              <Text fontSize="sm" color="gray.600" mx="auto">
                Haz clic en el mapa para colocar el contenedor. Puedes ajustar el centro inicial en la constante MAP_CENTER.
              </Text>
            </Field.Root>

            <Button
              type="submit"
              colorPalette="green"
              size="lg"
              isLoading={busy}
              loadingText="Guardando..."
              spinnerPlacement="end"
              alignSelf="flex-end"
              disabled={busy}
              px={2}
            >
              {submitText}
            </Button>

            {Object.values(errors).some((msg) => !!msg) && (
              <Text fontSize="sm" color="red.500">
                Corrige los campos marcados.
              </Text>
            )}
          </Stack>
        </form>
      </Box>
    </Stack>
  );
}
