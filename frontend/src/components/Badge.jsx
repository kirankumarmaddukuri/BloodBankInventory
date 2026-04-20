import React from 'react';

const Badge = ({ children, status }) => {
  const getBadgeStyle = (status) => {
    // Normalizing status to match design tokens
    const s = status?.toLowerCase() || '';

    switch (s) {
      case 'emergency':
      case 'critical':
        return 'bg-emergency/10 text-emergency border border-emergency/20';
      case 'urgent':
        return 'bg-urgent/10 text-urgent border border-urgent/20';
      case 'available':
      case 'eligible':
        return 'bg-available/10 text-available border border-available/20';
      case 'reserved':
        return 'bg-reserved/10 text-reserved border border-reserved/20';
      case 'expired':
        return 'bg-expired/20 text-[#D1D1D1] border border-expired/30';
      case 'discarded':
        return 'bg-discarded/10 text-discarded border border-discarded/20';
      case 'deferred':
      case 'temporarily_deferred':
      case 'permanently_deferred':
        return 'bg-urgent/10 text-urgent border border-urgent/20';
      default:
        return 'bg-navy border border-gray-600 text-gray-300';
    }
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold ${getBadgeStyle(status)}`}>
      {children || status}
    </span>
  );
};

export default Badge;
