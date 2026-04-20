import React from 'react';

const StatCard = ({ title, value, subtitle, layout = 'emergency' }) => {
  // We apply specific backgrounds matching the palette
  const getTheme = (layout) => {
    switch (layout.toLowerCase()) {
      case 'emergency':
        return {
          bg: 'bg-red-50', // Very light red background matching your text snippet mapping
          titleColor: 'text-primaryHover',
          valueColor: 'text-primary',
          subtitleColor: 'text-primaryHover'
        };
      case 'available':
        return {
          bg: 'bg-green-50',
          titleColor: 'text-available/80',
          valueColor: 'text-available',
          subtitleColor: 'text-available/80'
        };
      case 'urgent':
      case 'expiring':
        return {
          bg: 'bg-orange-50',
          titleColor: 'text-urgent',
          valueColor: 'text-urgent',
          subtitleColor: 'text-urgent/80'
        };
      case 'open':
      case 'info':
        return {
          bg: 'bg-blue-50',
          titleColor: 'text-reserved/80',
          valueColor: 'text-reserved',
          subtitleColor: 'text-reserved/80'
        };
      default:
        return {
          bg: 'bg-offWhite',
          titleColor: 'text-navy',
          valueColor: 'text-navy',
          subtitleColor: 'text-navy/70'
        };
    }
  };

  const theme = getTheme(layout);

  return (
    <div className={`${theme.bg} rounded-xl p-5 shadow-sm transition-all duration-300 hover:shadow-md`}>
      <h3 className={`text-sm font-semibold mb-2 ${theme.titleColor}`}>{title}</h3>
      <div className={`text-4xl font-bold mb-1 ${theme.valueColor}`}>{value}</div>
      <p className={`text-xs font-medium ${theme.subtitleColor}`}>{subtitle}</p>
    </div>
  );
};

export default StatCard;
