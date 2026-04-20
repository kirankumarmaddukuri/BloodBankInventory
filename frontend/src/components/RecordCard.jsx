import React from 'react';
import Badge from './Badge';

export const BloodUnitCard = ({ unit }) => {
  return (
    <div className="bg-[#242436] rounded-xl p-4 flex items-start flex-col sm:flex-row sm:items-center sm:justify-between border border-gray-700/50 shadow-md">
      <div className="flex flex-col">
        <div className="flex items-center gap-2 mb-2">
          <span className="text-white font-semibold text-lg tracking-wide">{unit.unitNumber}</span>
          <span className="text-gray-400">—</span>
          <span className="text-white font-bold text-lg">{unit.bloodGroup}</span>
          <span className="text-gray-300 text-sm ml-2">{unit.componentType.replace('_', ' ')} · {unit.volumeML} mL</span>
        </div>
        <div className="text-gray-400 text-xs font-medium tracking-wide">
          Collected {unit.collectionDate} · Expires {unit.expiryDate} · Storage: {unit.storageLocation || 'Unknown'}
        </div>
      </div>
      <div className="mt-4 sm:mt-0">
        <Badge status={unit.status} />
      </div>
    </div>
  );
};

export const EmergencyAlertCard = ({ alert }) => {
  return (
    <div className="bg-[#242436] rounded-xl p-4 flex items-start flex-col sm:flex-row sm:items-center sm:justify-between border-l-4 border-emergency shadow-md">
      <div className="flex flex-col">
        <div className="flex items-center gap-2 mb-2">
          <span className="text-white font-semibold text-lg tracking-wide">Emergency alert</span>
          <span className="text-gray-400">—</span>
          <span className="text-white font-bold text-lg">{alert.requiredBloodGroup}</span>
          <span className="text-gray-300 text-sm ml-2">{alert.unitsNeeded} units · {alert.location}</span>
        </div>
        <div className="text-gray-400 text-xs font-medium tracking-wide">
          Triggered {new Date(alert.triggeredAt).toLocaleString()}
        </div>
      </div>
      <div className="mt-4 sm:mt-0">
        <Badge status={alert.urgency}>{alert.urgency}</Badge>
      </div>
    </div>
  );
};
