export const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="custom-tooltip">
        <div className="label">{`${label}`}</div>
        <div className="value">{`Value: ${payload[0].value}`}</div>
      </div>
    );
  }
  return null;
};