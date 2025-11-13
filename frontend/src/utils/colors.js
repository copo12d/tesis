// Deterministic color mapping for arbitrary keys (e.g., container types)
// Ensures the same key always gets the same color across all charts.

// Optional base palette (used for visual consistency for common hashes)
export const BASE_PALETTE = [
  "#3e87e0", // blue
  "#ec1414", // red
  "#16a34a", // green
  "#f59e0b", // amber
  "#8b5cf6", // violet
  "#0ea5e9", // sky
  "#ef4444", // red-500
  "#22c55e", // green-500
  "#eab308", // yellow-500
  "#a855f7", // purple-500
  "#14b8a6", // teal-500
  "#f97316", // orange-500
];

// Simple deterministic hash for strings -> integer
function hashString(str) {
  let hash = 5381;
  for (let i = 0; i < str.length; i++) {
    hash = (hash * 33) ^ str.charCodeAt(i);
  }
  // Ensure positive 32-bit
  return hash >>> 0;
}

// Generate an HSL color string from a hash to cover large N types
function hslFromHash(hash) {
  const hue = hash % 360; // 0..359
  const saturation = 65; // percent
  const lightness = 52; // percent
  return `hsl(${hue} ${saturation}% ${lightness}%)`;
}

// Public API: get a stable color for a given key
export function colorForKey(key) {
  if (!key) return "#9ca3af"; // neutral gray fallback
  const normalized = String(key).trim().toLowerCase();
  const h = hashString(normalized);

  // Prefer base palette bucketed by hash to keep familiar tones, then fine-tune with HSL
  // Slightly rotate hue based on hash to reduce collisions when many categories share the same palette slot
  const hueVariant = hslFromHash(h);

  // Blend strategy: if you want strict palette only, return BASE_PALETTE[paletteIndex]
  // Returning HSL gives virtually unlimited distinct colors with stable mapping
  // To preserve the project's original palette feeling while scaling, combine both:
  // - Use base palette for first 12 common buckets
  // - For the rest, rely on HSL which is also deterministic
  // Here we choose HSL for maximum N support but keep the option documented above.
  return hueVariant;
}

// Convenience: map an array of keys to a dictionary of colors
export function mapColors(keys = []) {
  const out = {};
  keys.forEach(k => {
    out[k] = colorForKey(k);
  });
  return out;
}
