import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { LogOut, Activity, Flame, Droplet, User as UserIcon, Users, Search } from 'lucide-react';
import api from '../services/api';
import StatCard from '../components/StatCard';
import { BloodUnitCard, EmergencyAlertCard } from '../components/RecordCard';

const Dashboard = () => {
  const { user, logout } = useAuth();

  const [inventory, setInventory] = useState([]);
  const [alerts, setAlerts] = useState([]);
  
  // Date state for Donor Scheduling
  const [appointmentDate, setAppointmentDate] = useState('');

  // States for Admin Panel 4.1 UI additions
  const [targetRole, setTargetRole] = useState('DONOR');
  const [userList, setUserList] = useState([]);
  const [targetBlood, setTargetBlood] = useState('O_POS');
  const [donorList, setDonorList] = useState([]);
  
  // Blood Bank Admin actions
  const [recDonorId, setRecDonorId] = useState('');
  const [recComponent, setRecComponent] = useState('WHOLE_BLOOD');
  const [recVolume, setRecVolume] = useState(350);
  const [recWeight, setRecWeight] = useState(55);
  const [recHemo, setRecHemo] = useState(13.5);

  const [eligibilityDonorId, setEligibilityDonorId] = useState('');
  const [eligibilityStatus, setEligibilityStatus] = useState('ELIGIBLE');
  
  // Hospital Staff - Request Blood
  const [reqHospital, setReqHospital] = useState('');
  const [reqPatient, setReqPatient] = useState('');
  const [reqBloodGroup, setReqBloodGroup] = useState('O_POS');
  const [reqBloodComp, setReqBloodComp] = useState('WHOLE_BLOOD');
  const [reqUnitsNeeded, setReqUnitsNeeded] = useState(1);
  const [reqPriority, setReqPriority] = useState('ROUTINE');

  // Hospital Staff - Log Transfusion
  const [transUnitId, setTransUnitId] = useState('');
  const [transReqId, setTransReqId] = useState('');
  const [transPatient, setTransPatient] = useState('');
  const [transHospital, setTransHospital] = useState('');
  const [transBy, setTransBy] = useState('');
  const [transAdverse, setTransAdverse] = useState(false);
  const [transRemarks, setTransRemarks] = useState('');

  // Emergency Coordinator States
  const [emgBloodGroup, setEmgBloodGroup] = useState('O_NEG');
  const [emgComponent, setEmgComponent] = useState('WHOLE_BLOOD');
  const [emgUnits, setEmgUnits] = useState(1);
  const [emgLocation, setEmgLocation] = useState('');
  const [emgUrgency, setEmgUrgency] = useState('CRITICAL');
  const [donorMatches, setDonorMatches] = useState({});

  // Hospital Staff extra state
  const [myRequests, setMyRequests] = useState([]);
  const [pubInventory, setPubInventory] = useState([]);

  // Dispatcher extra state
  const [dispatcherInventory, setDispatcherInventory] = useState([]);
  const [allDonors, setAllDonors] = useState([]);

  // States for Donor History
  const [donationHistory, setDonationHistory] = useState([]);
  const [donorNotifications, setDonorNotifications] = useState([]);

  const [testingUnits, setTestingUnits] = useState([]);
  const [donorProfile, setDonorProfile] = useState(null);
  
  // Hospital Requests state
  const [bloodRequests, setBloodRequests] = useState([]);
  
  // Dashboard Metrics state
  const [expiringCount, setExpiringCount] = useState(0);
  const [allAppointments, setAllAppointments] = useState([]);

  useEffect(() => {
    if (user?.role === 'BLOOD_BANK_ADMIN' || user?.role === 'ADMIN') {
      api.get('/blood-units/inventory').then(res => setInventory(res.data)).catch(console.error);
      api.get('/blood-units/testing').then(res => setTestingUnits(res.data)).catch(console.error);
      api.get('/blood-requests').then(res => { console.log('Admin Blood Requests:', res.data); setBloodRequests(res.data); }).catch(console.error);
      api.get('/blood-units/expiring').then(res => setExpiringCount(res.data.length)).catch(console.error);
      api.get('/appointments').then(res => setAllAppointments(res.data)).catch(console.error);
    }
    if (user?.role === 'HOSPITAL_STAFF') {
      api.get('/blood-requests').then(res => { console.log('Hospital My Requests:', res.data); setMyRequests(res.data); }).catch(console.error);
      api.get('/blood-units/inventory').then(res => setPubInventory(res.data)).catch(console.error);
    }
    if (user?.role === 'EMERGENCY_COORDINATOR') {
      api.get('/emergency-alerts/active').then(res => setAlerts(res.data)).catch(console.error);
      api.get('/blood-units/inventory').then(res => setDispatcherInventory(res.data)).catch(console.error);
      api.get('/donors').then(res => setAllDonors(res.data)).catch(console.error);
    }
    if (user?.role === 'DONOR') {
      api.get('/donors/my-history').then(res => setDonationHistory(res.data)).catch(console.error);
      api.get('/donors/my-profile').then(res => setDonorProfile(res.data)).catch(console.error);
      api.get('/donors/notifications').then(res => setDonorNotifications(res.data)).catch(console.error);
    }
  }, [user]);

  const fetchUsers = () => {
    api.get(`/users?role=${targetRole}`)
       .then(res => setUserList(res.data))
       .catch(err => alert("Failed to fetch users: " + (err.response?.data?.message || err.message)));
  };

  const fetchDonors = () => {
    api.get(`/donors/eligible?bloodGroup=${targetBlood}`)
       .then(res => setDonorList(res.data))
       .catch(err => alert("Failed to fetch donors: " + (err.response?.data?.message || err.message)));
  };

  const handleRecordDonation = () => {
    if(!recDonorId) { alert('Enter Donor ID'); return; }
    
    const payload = {
        donorId: Number(recDonorId), 
        componentType: recComponent, 
        volumeML: Number(recVolume), 
        donorWeight: Number(recWeight), 
        donorHemoglobin: Number(recHemo)
    };

    api.post('/blood-units', payload)
       .then(res => {
         alert('Blood unit ' + res.data.unitNumber + ' recorded successfully! It has been moved to the Medical Testing Lab.');
         setRecDonorId('');
         window.location.reload();
       })
       .catch(err => alert("Record failed: " + (err.response?.data?.message || err.message)));
  };

  const handleUpdateEligibility = () => {
    if(!eligibilityDonorId) { alert('Enter Donor ID'); return; }
    api.put(`/donors/${eligibilityDonorId}/eligibility`, { status: eligibilityStatus })
       .then(res => {
         alert(`Donor ${res.data.id} state updated to ${res.data.eligibilityStatus}`);
         setEligibilityDonorId('');
       })
       .catch(err => alert("Eligibility update failed: " + (err.response?.data?.message || err.message)));
  };

  const handleSchedule = () => {
    if(!appointmentDate) { 
      alert('Please select a date and time!'); 
      return; 
    }
    
    // The datetime-local input gives YYYY-MM-DDTHH:mm, but Spring Boot expects seconds too.
    const formattedDate = appointmentDate.length === 16 ? appointmentDate + ':00' : appointmentDate;

    api.post('/appointments', { appointmentDate: formattedDate, notes: 'Routine Donation via Web' })
      .then(res => {
         alert('Successfully booked for ' + new Date(res.data.appointmentDate).toLocaleString() + '!');
         setAppointmentDate('');
      })
      .catch(err => {
         console.error(err.response);
         const data = err.response?.data;
         const errorMessage = data?.message || data?.appointmentDate || JSON.stringify(data) || err.message;
         alert("Booking failed: " + errorMessage);
      });
  };

  const handleCreateRequest = () => {
    if(!reqHospital || !reqPatient) { alert('Fill all request fields'); return; }
    api.post('/blood-requests', {
        requestingHospital: reqHospital,
        patientName: reqPatient,
        patientBloodGroup: reqBloodGroup,
        componentType: reqBloodComp,
        unitsRequested: Number(reqUnitsNeeded),
        priority: reqPriority
    }).then(res => {
        alert('Request #' + res.data.id + ' successfully transmitted to the regional bank!');
        window.location.reload();
    }).catch(err => alert("Failed: " + JSON.stringify(err.response?.data)));
  };

  const handleLogTransfusion = () => {
    if(!transUnitId || !transReqId || !transPatient) { alert('Missing core identifiers'); return; }
    api.post('/transfusions', {
        bloodUnitId: Number(transUnitId),
        requestId: Number(transReqId),
        patientName: transPatient,
        hospital: transHospital,
        transfusedBy: transBy,
        adverseReaction: transAdverse,
        reactionRemarks: transRemarks
    }).then(res => {
        alert('Transfusion securely recorded! Tracking ID: ' + res.data.id);
        window.location.reload();
    }).catch(err => alert("Failed: " + (err.response?.data?.message || JSON.stringify(err.response?.data))));
  };

  const handleTriggerAlert = () => {
    if(!emgLocation) { alert('Specify location'); return; }
    api.post('/emergency-alerts', {
       requiredBloodGroup: emgBloodGroup,
       requiredComponentType: emgComponent,
       unitsNeeded: Number(emgUnits),
       location: emgLocation,
       urgency: emgUrgency
    }).then(() => {
       alert('EMERGENCY BROADCAST TRANSMITTED NATIONWIDE!');
       window.location.reload();
    }).catch(err => alert("Failed: " + JSON.stringify(err.response?.data)));
  };

  const loadDonorMatches = (alertId) => {
     api.get(`/emergency-alerts/${alertId}/donor-match`).then(res => {
         setDonorMatches(prev => ({...prev, [alertId]: res.data}));
     }).catch(console.error);
  };

  const renderRoleDashboard = () => {
    switch (user?.role) {
      case 'DONOR':
        return (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <StatCard title="Total Donations" value={donorProfile?.totalDonations?.toString() || '0'} subtitle="Thank you for saving lives" layout="emergency" />
            <StatCard title="Eligibility" value={donorProfile?.eligibilityStatus?.replace('_', ' ') || '...'} subtitle="Your current status" layout="available" />
            
            {/* Donor Emergency Pings */}
            {donorNotifications.filter(n => n.responseStatus === 'PENDING').length > 0 && (
               <div className="col-span-full bg-red-900/30 border border-red-500 rounded-xl p-6 shadow-red-500/10 shadow-lg mb-4">
                 <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2 animate-pulse"><Flame className="text-red-500"/> URGENT: EMERGENCY BROADCAST</h3>
                 <div className="space-y-3">
                   {donorNotifications.filter(n => n.responseStatus === 'PENDING').map(notif => (
                      <div key={notif.id} className="bg-[#242436] p-4 rounded-lg flex flex-col sm:flex-row justify-between items-center border border-red-500/20 gap-4">
                        <div className="w-full">
                           <p className="text-red-400 font-bold mb-1">Mass Casualty / Critical Shortage at {notif.emergencyAlert?.location}</p>
                           <p className="text-gray-300 text-sm">They urgently need {notif.emergencyAlert?.unitsNeeded}x {notif.emergencyAlert?.requiredBloodGroup?.replace('_POS','+').replace('_NEG','-')} units.</p>
                        </div>
                        <div className="flex gap-2 w-full sm:w-auto">
                           <button onClick={() => {
                               api.put(`/donors/notifications/${notif.id}/respond?response=ACCEPTED`).then(() => window.location.reload());
                           }} className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 font-bold rounded w-full sm:w-auto shrink-0 shadow-lg shadow-red-500/20">I CAN DONATE</button>
                           <button onClick={() => {
                               api.put(`/donors/notifications/${notif.id}/respond?response=DECLINED`).then(() => window.location.reload());
                           }} className="bg-[#1A1A2E] hover:bg-gray-700 border border-gray-600 text-white px-4 py-2 font-bold rounded w-full sm:w-auto shrink-0">Decline</button>
                        </div>
                      </div>
                   ))}
                 </div>
               </div>
            )}

            <div className="col-span-full mt-4 bg-[#242436] p-6 rounded-xl border border-gray-700/50 shadow-md">
              <h3 className="text-white font-bold text-xl mb-4">Schedule a Donation</h3>
              <div className="flex flex-col sm:flex-row gap-4 items-end">
                <div className="flex-1 w-full">
                  <label className="block text-gray-400 text-xs font-semibold mb-1 uppercase tracking-wider">Select Date & Time</label>
                  <input type="datetime-local" value={appointmentDate} onChange={e => setAppointmentDate(e.target.value)}
                    className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-600 transition-colors [color-scheme:dark]" />
                </div>
                <button onClick={handleSchedule} className="bg-primary hover:bg-primaryHover text-white px-8 py-3 rounded-lg shadow-lg font-bold w-full sm:w-auto h-[48px] transition-all active:scale-95">
                  Confirm Booking
                </button>
              </div>
            </div>

            <div className="col-span-full mt-4 bg-[#1A1A2E] p-6 rounded-xl border border-gray-700/50 shadow-inner">
              <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><Activity className="text-green-400"/> My Donation History</h3>
              <div className="space-y-3 max-h-60 overflow-y-auto pr-2">
                {donationHistory.length === 0 ? (
                   <p className="text-gray-400 text-sm italic">You haven't completed any donations yet. Schedule yours today!</p>
                ) : (
                   donationHistory.map(item => (
                     <div key={item.id} className="bg-[#242436] p-4 rounded-lg flex justify-between items-center border border-gray-700">
                       <div>
                         <p className="font-semibold text-white tracking-wide">{item.unitNumber} <span className="text-xs text-gray-400 ml-2 font-normal">Vol: {item.volumeML} mL</span></p>
                         <p className="text-xs text-gray-400 mt-1">{new Date(item.collectionDate).toDateString()} • {item.componentType}</p>
                       </div>
                       <span className={`px-3 py-1 border rounded-full text-[10px] font-bold uppercase tracking-wider ${item.status === 'AVAILABLE' || item.status === 'ISSUED' || item.status === 'COLLECTED' ? 'bg-green-500/10 text-green-400 border-green-500/30' : 'bg-red-500/10 text-red-400 border-red-500/30'}`}>
                         {item.status.replace('_', ' ')}
                       </span>
                     </div>
                   ))
                )}
              </div>
            </div>
          </div>
        );
      
      case 'BLOOD_BANK_ADMIN':
      case 'ADMIN':
        return (
          <div className="space-y-8">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              <StatCard title="Critical Stock" value={inventory.filter(u => u.bloodGroup === 'O_NEG').length} subtitle="O- units left" layout="emergency" />
              <StatCard title="Available" value={inventory.length} subtitle="total units ready" layout="available" />
              <StatCard title="Expiring Soon" value={expiringCount} subtitle="within 7 days" layout="expiring" />
              <StatCard title="Open Requests" value={bloodRequests.filter(r => r.status === 'PENDING').length} subtitle="pending requests" layout="open" />
            </div>

            {/* Admin Management Panels Section */}
            {(user?.role === 'ADMIN' || user?.role === 'BLOOD_BANK_ADMIN') && (
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-8">
                {/* User Search by Role */}
                <div className="bg-[#242436] rounded-xl p-6 border border-gray-700/50 shadow-md">
                   <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><Users className="text-primary"/> Fetch Users by Role</h3>
                   <div className="flex gap-3 mb-4">
                     <select value={targetRole} onChange={e => setTargetRole(e.target.value)} 
                        className="flex-1 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-600">
                        <option value="DONOR">Donor</option>
                        <option value="HOSPITAL_STAFF">Hospital Staff</option>
                        <option value="BLOOD_BANK_ADMIN">Blood Bank Admin</option>
                        <option value="EMERGENCY_COORDINATOR">Dispatcher</option>
                        <option value="ADMIN">Admin</option>
                     </select>
                     <button onClick={fetchUsers} className="bg-primary hover:bg-primaryHover text-white px-4 py-2 rounded-lg flex items-center gap-2 font-bold transition-all">
                       <Search size={18}/> Fetch
                     </button>
                   </div>
                   <div className="space-y-2 max-h-48 overflow-y-auto">
                     {userList.length === 0 && <p className="text-gray-400 text-sm italic">No users fetched yet.</p>}
                     {userList.map(u => (
                        <div key={u.id} className="bg-[#1A1A2E] p-3 rounded-lg border border-gray-700 flex justify-between items-center">
                          <div>
                            <p className="text-white font-semibold text-sm">{u.name}</p>
                            <p className="text-gray-400 text-xs">{u.email}</p>
                          </div>
                          <span className="text-primary text-xs font-bold tracking-wider">{u.role}</span>
                        </div>
                     ))}
                   </div>
                </div>

                {/* Eligible Donors Search by Blood Group */}
                <div className="bg-[#242436] rounded-xl p-6 border border-gray-700/50 shadow-md">
                   <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><Droplet className="text-available"/> Eligible Donors Query</h3>
                   <div className="flex gap-3 mb-4">
                     <select value={targetBlood} onChange={e => setTargetBlood(e.target.value)} 
                        className="flex-1 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-available border border-gray-600">
                        <option value="O_POS">O+</option>
                        <option value="A_POS">A+</option>
                        <option value="B_POS">B+</option>
                        <option value="AB_POS">AB+</option>
                        <option value="O_NEG">O-</option>
                        <option value="A_NEG">A-</option>
                        <option value="B_NEG">B-</option>
                        <option value="AB_NEG">AB-</option>
                     </select>
                     <button onClick={fetchDonors} className="bg-available hover:bg-green-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 font-bold transition-all">
                       <Search size={18}/> Fetch
                     </button>
                   </div>
                   <div className="space-y-2 max-h-48 overflow-y-auto">
                     {donorList.length === 0 && <p className="text-gray-400 text-sm italic">No donors fetched yet.</p>}
                     {donorList.map(d => (
                        <div key={d.id} className="bg-[#1A1A2E] p-3 rounded-lg border border-gray-700 flex justify-between items-center">
                          <div>
                            <p className="text-white font-semibold text-sm mx-1">{d.user?.name || "Unknown"} <span className="text-gray-400 text-xs ml-2">Donations: {d.totalDonations}</span></p>
                          </div>
                          <span className="bg-available/20 text-available border border-available/30 px-2 py-1 rounded-full text-xs font-bold w-12 text-center">{d.bloodGroup.replace('_POS','+').replace('_NEG','-')}</span>
                        </div>
                     ))}
                   </div>
                </div>

                {/* Record Donation Panel */}
                <div id="record-donation-panel" className="bg-[#242436] rounded-xl p-6 border border-gray-700/50 shadow-md transition-all">
                   <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><Activity className="text-red-400"/> Record Donation Unit</h3>
                   <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-4">
                     <input type="number" placeholder="Donor ID" value={recDonorId} onChange={e => setRecDonorId(e.target.value)} 
                        className="bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-red-400 border border-gray-600" />
                     <select value={recComponent} onChange={e => setRecComponent(e.target.value)} 
                        className="col-span-1 sm:col-span-2 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-red-400 border border-gray-600">
                        <option value="WHOLE_BLOOD">Whole Blood</option>
                        <option value="RBC">Red Blood Cells (RBC)</option>
                        <option value="PLATELETS">Platelets</option>
                        <option value="PLASMA">Plasma</option>
                     </select>
                     <input type="number" placeholder="Vol (mL)" value={recVolume} onChange={e => setRecVolume(e.target.value)} title="Volume ML (Requires 350-450)"
                        className="bg-[#1A1A2E] text-white text-sm rounded-lg p-3 outline-none focus:ring-2 border border-gray-600" />
                     <input type="number" step="0.1" placeholder="Weight kg" value={recWeight} onChange={e => setRecWeight(e.target.value)} title="Weight (Requires >50kg)"
                        className="bg-[#1A1A2E] text-white text-sm rounded-lg p-3 outline-none focus:ring-2 border border-gray-600" />
                     <input type="number" step="0.1" placeholder="Hemo g/dL" value={recHemo} onChange={e => setRecHemo(e.target.value)} title="Hemoglobin (Requires >12.5)"
                        className="bg-[#1A1A2E] text-white text-sm rounded-lg p-3 outline-none focus:ring-2 border border-gray-600" />
                   </div>
                   <button onClick={handleRecordDonation} className="w-full bg-red-500 hover:bg-red-600 text-white p-3 rounded-lg font-bold transition-all shadow-lg">
                     Execute Registration & Store Unit
                   </button>
                </div>

                {/* Update Eligibility Panel */}
                <div className="bg-[#242436] rounded-xl p-6 border border-gray-700/50 shadow-md">
                   <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><UserIcon className="text-orange-400"/> Override Donor Status</h3>
                   <div className="flex flex-col gap-3">
                     <input type="number" placeholder="Target Donor ID" value={eligibilityDonorId} onChange={e => setEligibilityDonorId(e.target.value)} 
                        className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-orange-400 border border-gray-600" />
                     <select value={eligibilityStatus} onChange={e => setEligibilityStatus(e.target.value)} 
                        className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-orange-400 border border-gray-600">
                        <option value="ELIGIBLE">Eligible (Clear)</option>
                        <option value="TEMPORARILY_DEFERRED">Temporarily Deferred</option>
                        <option value="PERMANENTLY_DEFERRED">Permanently Deferred</option>
                     </select>
                     <button onClick={handleUpdateEligibility} className="w-full bg-orange-500 hover:bg-orange-600 text-white p-3 rounded-lg font-bold transition-all shadow-lg">
                       Update Global Status
                     </button>
                </div>

                {/* Upcoming Appointments List */}
                <div className="bg-[#242436] rounded-xl p-6 border border-gray-700/50 shadow-md col-span-1 lg:col-span-2">
                   <div className="flex justify-between items-center mb-4">
                      <h3 className="text-white font-bold text-xl flex items-center gap-2"><Droplet className="text-blue-400"/> Upcoming Donor Schedule</h3>
                      <button onClick={() => api.get('/appointments').then(res => setAllAppointments(res.data))} className="text-xs bg-blue-500/10 hover:bg-blue-500/20 text-blue-300 px-3 py-1 border border-blue-500/30 rounded-lg transition-all font-bold">
                        ↻ Refresh Live
                      </button>
                   </div>
                   <div className="overflow-x-auto">
                     <table className="w-full text-left text-gray-300 text-sm">
                        <thead className="bg-[#1A1A2E] text-gray-400 uppercase text-[10px] font-bold">
                           <tr>
                              <th className="px-4 py-2">Date</th>
                              <th className="px-4 py-2">Donor</th>
                              <th className="px-4 py-2">Blood Group</th>
                              <th className="px-4 py-2">Notes</th>
                              <th className="px-4 py-2">Status</th>
                           </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-800">
                           {allAppointments.length === 0 ? (
                             <tr><td colSpan="5" className="p-4 italic text-center">No upcoming appointments scheduled.</td></tr>
                           ) : (
                             allAppointments.map(appt => (
                               <tr key={appt.id} className="hover:bg-white/5">
                                 <td className="px-4 py-3 font-semibold">{new Date(appt.appointmentDate).toLocaleDateString()}</td>
                                 <td className="px-4 py-3 text-white">{appt.donor?.user?.name}</td>
                                 <td className="px-4 py-3"><span className="bg-primary/20 text-primary px-2 py-0.5 rounded text-xs font-bold">{appt.donor?.bloodGroup?.replace('_POS','+').replace('_NEG','-')}</span></td>
                                 <td className="px-4 py-3 max-w-[200px] truncate">{appt.notes || '—'}</td>
                                 <td className="px-4 py-3"><span className="text-available text-[10px] font-bold uppercase tracking-wider">{appt.status}</span></td>
                               </tr>
                             ))
                           )}
                        </tbody>
                     </table>
                   </div>
                </div>
                </div>

                {/* Testing Lab Panel */}
                <div className="bg-[#1A1A2E] rounded-xl p-6 border border-gray-700/50 shadow-inner col-span-1 lg:col-span-2 mt-4">
                   <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><Flame className="text-yellow-400"/> Medical Testing Lab</h3>
                   <div className="space-y-2 max-h-48 overflow-y-auto pr-2">
                     {testingUnits.length === 0 ? (
                        <p className="text-gray-400 text-sm italic">All captured units have been fully tested and sorted.</p>
                     ) : (
                        testingUnits.map(unit => (
                          <div key={unit.id} className="bg-[#242436] p-4 rounded-lg flex justify-between items-center border border-yellow-500/30">
                            <div>
                               <p className="font-semibold text-white tracking-wide">{unit.unitNumber} <span className="text-[10px] text-yellow-400 bg-yellow-400/10 border border-yellow-500/30 px-2 py-0.5 rounded-full ml-2 font-bold uppercase tracking-wider">Unscreened</span></p>
                               <p className="text-xs text-gray-400 mt-1">Vol: {unit.volumeML} mL • Collected: {new Date(unit.collectionDate).toDateString()}</p>
                            </div>
                            <button onClick={() => {
                                api.put(`/blood-units/${unit.id}/test`, { testedForHiv: false, testedForHepatitis: false, testedForMalaria: false, testedForSyphilis: false })
                                   .then(() => {
                                      alert(`Unit ${unit.unitNumber} marked SAFE for transfusion! Moved to Available Ledger.`);
                                      window.location.reload();
                                   })
                                   .catch(err => alert("Testing failed: " + JSON.stringify(err.response?.data)));
                            }} className="bg-yellow-500 hover:bg-yellow-400 text-black px-4 py-2 font-bold text-xs uppercase tracking-wider rounded-lg transition-all shadow-md">
                               Screen & Clear
                            </button>
                          </div>
                        ))
                     )}
                   </div>
                </div>

                {/* Hospital Requests Panel */}
                <div className="bg-[#1A1A2E] rounded-xl p-6 border border-gray-700/50 shadow-inner col-span-1 lg:col-span-2 mt-4">
                   <div className="flex justify-between items-center mb-4">
                      <h3 className="text-white font-bold text-xl flex items-center gap-2"><Activity className="text-purple-400"/> Hospital Requests Command</h3>
                      <button onClick={() => { console.log("Syncing requests..."); api.get('/blood-requests').then(res => setBloodRequests(res.data)); }} className="text-xs bg-purple-500/10 hover:bg-purple-500/20 text-purple-300 px-3 py-1 border border-purple-500/30 rounded-lg transition-all font-bold">
                        ↻ Sync Requests
                      </button>
                   </div>
                   <div className="space-y-3 max-h-80 overflow-y-auto pr-2">
                     <p className="text-[10px] text-gray-500 mb-1 uppercase tracking-widest font-bold">Loaded: {bloodRequests.length} records</p>
                     {bloodRequests.length === 0 ? (
                        <p className="text-gray-400 text-sm italic">No requisitions currently tracked on the grid.</p>
                     ) : (
                        bloodRequests.map(req => (
                          <div key={req.id} className="bg-[#242436] p-4 rounded-lg flex flex-col sm:flex-row justify-between items-center border border-purple-500/30 gap-4 shadow-sm">
                            <div className="w-full">
                               <div className="flex items-center gap-2 mb-1">
                                 <p className="font-semibold text-white tracking-wide">{req.requestingHospital} <span className="text-gray-400 font-normal">({req.patientName})</span></p>
                                 <span className={`px-2 py-0.5 border rounded-full text-[10px] font-bold tracking-wider ${req.status === 'PENDING' ? 'bg-orange-500/10 text-orange-400 border-orange-500/30' : req.status === 'PROCESSING' ? 'bg-blue-500/10 text-blue-400 border-blue-500/30' : 'bg-green-500/10 text-green-400 border-green-500/30'}`}>
                                    {req.status}
                                 </span>
                                 {req.priority === 'EMERGENCY' && <span className="bg-red-500/20 text-red-500 border border-red-500/30 px-2 py-0.5 rounded-full text-[10px] font-bold tracking-wider">EMERGENCY 🚨</span>}
                               </div>
                               <p className="text-xs text-gray-400">Requisition: <span className="text-white font-bold text-sm tracking-wide">{req.unitsRequested}x {req.patientBloodGroup?.replace('_POS','+').replace('_NEG','-')} {req.componentType}</span></p>
                            </div>
                            <div className="flex gap-2 w-full sm:w-auto shrink-0 mt-2 sm:mt-0">
                               {req.status === 'PENDING' && (
                                  <button onClick={() => {
                                      api.put(`/blood-requests/${req.id}/process`).then(() => {
                                          alert('Requisition status pushed to PROCESSING. Begin unit verification.');
                                          window.location.reload();
                                      }).catch(err => alert("Update Failed: " + JSON.stringify(err.response?.data)));
                                  }} className="bg-blue-500 hover:bg-blue-400 text-white shadow-lg shadow-blue-500/20 px-6 py-2 pb-[10px] font-bold text-xs uppercase tracking-wider rounded-lg transition-all w-full">
                                     Acknowledge & Process
                                  </button>
                               )}
                               {req.status === 'PROCESSING' && (
                                  <button onClick={() => {
                                      api.put(`/blood-requests/${req.id}/fulfill`).then(() => {
                                          alert('Success! The system automatically verified compatibility matrix algorithms, allocated exactly ' + req.unitsRequested + ' unit(s) from inventory, updated stock, and fulfilled the hospital request!');
                                          window.location.reload();
                                      }).catch(err => alert("Allocation Failed: " + (err.response?.data?.message || JSON.stringify(err.response?.data))));
                                  }} className="bg-green-500 hover:bg-green-400 text-white shadow-lg shadow-green-500/20 px-6 py-2 pb-[10px] font-bold text-xs uppercase tracking-wider rounded-lg transition-all w-full">
                                     Allocate & Fulfill Order
                                  </button>
                               )}
                            </div>
                          </div>
                        ))
                     )}
                   </div>
                </div>

              </div>
            )}

            <div className="bg-[#1A1A2E] border border-gray-700/50 rounded-xl p-6 mt-8">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-white font-bold text-xl flex items-center gap-2"><Droplet className="text-primary"/> Inventory Units</h3>
                <button onClick={() => {
                     const panel = document.getElementById('record-donation-panel');
                     if(panel) {
                         panel.scrollIntoView({behavior: 'smooth', block: 'center'});
                         panel.style.boxShadow = "0 0 20px 2px rgba(239, 68, 68, 0.4)";
                         setTimeout(() => panel.style.boxShadow = "none", 1500);
                     }
                }} className="text-sm bg-red-500/20 hover:bg-red-500/40 text-red-200 px-4 py-2 border border-red-500/30 rounded-lg transition-colors font-bold shadow-lg">
                  + Add Unit ⬆
                </button>
              </div>
              <div className="space-y-4">
                {inventory.length === 0 ? (
                   <p className="text-gray-400 text-sm italic">Inventory is completely empty. Please use the "Record Donation Unit" tool above to securely log new blood units into the ledger.</p>
                ) : (
                  inventory.slice(0, 5).map(unit => <BloodUnitCard key={unit.id} unit={unit} />)
                )}
              </div>
            </div>
          </div>
        );


      case 'EMERGENCY_COORDINATOR':
        return (
          <div className="space-y-8">
            <div className="bg-[#242436] p-6 rounded-xl border border-red-500/50 shadow-md">
               <h3 className="text-white font-bold text-2xl mb-4 flex items-center gap-2"><Flame className="text-red-500 animate-pulse"/> Broadcast Emergency Alert</h3>
               <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                  <select value={emgBloodGroup} onChange={e => setEmgBloodGroup(e.target.value)} className="bg-[#1A1A2E] text-white rounded-lg p-3 border border-red-500/30 font-semibold focus:outline-none focus:ring-1 focus:ring-red-500 cursor-pointer">
                      <option value="O_NEG">O- (Universal)</option><option value="O_POS">O+</option><option value="A_NEG">A-</option><option value="A_POS">A+</option>
                      <option value="B_NEG">B-</option><option value="B_POS">B+</option><option value="AB_NEG">AB-</option><option value="AB_POS">AB+</option>
                  </select>
                  <select value={emgComponent} onChange={e => setEmgComponent(e.target.value)} className="bg-[#1A1A2E] text-white rounded-lg p-3 border border-red-500/30 font-semibold focus:outline-none focus:ring-1 focus:ring-red-500 cursor-pointer">
                      <option value="WHOLE_BLOOD">Whole Blood</option><option value="RBC">RBC</option><option value="PLATELETS">Platelets</option>
                  </select>
                  <input type="number" placeholder="Units required" min="1" value={emgUnits} onChange={e => setEmgUnits(e.target.value)} className="bg-[#1A1A2E] text-white rounded-lg p-3 border border-red-500/30 focus:outline-none focus:ring-1 focus:ring-red-500" />
                  <input type="text" placeholder="Hospital / Location" value={emgLocation} onChange={e => setEmgLocation(e.target.value)} className="bg-[#1A1A2E] text-white rounded-lg p-3 border border-red-500/30 focus:outline-none focus:ring-1 focus:ring-red-500" />
               </div>
               <button onClick={handleTriggerAlert} className="w-full bg-red-600 hover:bg-red-700 text-white font-black py-4 rounded-lg shadow-[0_0_15px_rgba(220,38,38,0.5)] transition-all uppercase tracking-widest text-lg active:scale-[0.99]">
                 INITIATE NATIONWIDE TRIGGER ALERT
               </button>
            </div>

            {/* Stats Overview */}
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              <StatCard title="Active Alerts" value={alerts.length.toString()} subtitle="live crises" layout="emergency" />
              <StatCard title="Total Inventory" value={dispatcherInventory.length.toString()} subtitle="available units" layout="available" />
              <StatCard title="O- Critical" value={dispatcherInventory.filter(u=>u.bloodGroup==='O_NEG').length.toString()} subtitle="universal donor" layout="emergency" />
              <StatCard title="Eligible Donors" value={allDonors.filter(d=>d.eligibilityStatus==='ELIGIBLE').length.toString()} subtitle="ready to donate" layout="open" />
            </div>

            {/* Blood Stock Overview */}
            <div className="bg-[#242436] p-6 rounded-xl border border-gray-700/50">
              <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><Droplet className="text-primary"/> Live Blood Stock Overview</h3>
              <div className="grid grid-cols-4 sm:grid-cols-8 gap-2">
                {['O_NEG','O_POS','A_POS','A_NEG','B_POS','B_NEG','AB_POS','AB_NEG'].map(bg => {
                  const count = dispatcherInventory.filter(u => u.bloodGroup === bg).length;
                  return <div key={bg} className={`rounded-xl p-3 text-center border ${count === 0 ? 'bg-red-900/30 border-red-500/40' : count < 3 ? 'bg-orange-900/20 border-orange-500/30' : 'bg-green-900/10 border-green-500/20'}`}><p className="text-xl font-black text-white">{count}</p><p className={`text-[10px] font-bold tracking-wider mt-1 ${count === 0 ? 'text-red-400' : count < 3 ? 'text-orange-400' : 'text-green-400'}`}>{bg.replace('_POS','+').replace('_NEG','-')}</p></div>;
                })}
              </div>
            </div>

            {/* Eligible Donor Pool */}
            <div className="bg-[#242436] p-6 rounded-xl border border-gray-700/50">
              <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><UserIcon className="text-green-400"/> Eligible Donor Pool</h3>
              <div className="space-y-2 max-h-52 overflow-y-auto pr-1">
                {allDonors.filter(d => d.eligibilityStatus === 'ELIGIBLE').length === 0 ? <p className="text-gray-400 italic text-sm">No eligible donors.</p> : allDonors.filter(d => d.eligibilityStatus === 'ELIGIBLE').map(d => (
                    <div key={d.id} className="bg-[#1A1A2E] p-3 rounded-lg flex justify-between items-center border border-gray-700">
                      <div><p className="text-white font-semibold text-sm">{d.user?.name}</p><p className="text-gray-400 text-xs">{d.user?.phone} · {d.totalDonations} donations</p></div>
                      <span className="bg-red-500/20 text-red-300 border border-red-500/30 px-2 py-0.5 rounded-full text-xs font-bold">{d.bloodGroup?.replace('_POS','+').replace('_NEG','-')}</span>
                    </div>
                ))}
              </div>
            </div>

            <div className="bg-[#242436] p-6 rounded-xl border border-gray-700/50 shadow-md">
               <h3 className="text-white font-bold text-xl mb-4">Active Crisis Networks</h3>
               <div className="space-y-4">
                  {alerts.length === 0 ? (
                    <EmergencyAlertCard alert={{ requiredBloodGroup: 'AB-', unitsNeeded: 4, location: 'City Hospital', triggeredAt: new Date(Date.now() - 120000).toISOString(), urgency: 'CRITICAL' }} />
                  ) : (
                    alerts.map(a => <EmergencyAlertCard key={a.id} alert={a} />)
                  )}
               </div>
            </div>
          </div>
        );

      case 'HOSPITAL_STAFF':
        return (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="bg-[#242436] p-6 rounded-xl border border-gray-700/50 shadow-md">
               <h3 className="text-white font-bold text-xl mb-2 flex items-center gap-2"><Activity className="text-urgent"/> Request Blood</h3>
               <p className="text-gray-400 text-sm mb-6">Submit an urgent or routine request to the regional bank.</p>
               
               <div className="space-y-3 mb-4">
                 <input type="text" placeholder="Hospital Name" value={reqHospital} onChange={e => setReqHospital(e.target.value)} 
                    className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-urgent border border-gray-600" />
                 <input type="text" placeholder="Patient Name" value={reqPatient} onChange={e => setReqPatient(e.target.value)} 
                    className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-urgent border border-gray-600" />
                 
                 <div className="flex gap-2">
                    <select value={reqBloodGroup} onChange={e => setReqBloodGroup(e.target.value)} className="w-1/2 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none border border-gray-600">
                      <option value="O_POS">O+</option><option value="A_POS">A+</option><option value="B_POS">B+</option><option value="AB_POS">AB+</option>
                      <option value="O_NEG">O-</option><option value="A_NEG">A-</option><option value="B_NEG">B-</option><option value="AB_NEG">AB-</option>
                    </select>
                    <select value={reqBloodComp} onChange={e => setReqBloodComp(e.target.value)} className="w-1/2 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none border border-gray-600">
                      <option value="WHOLE_BLOOD">Whole Blood</option><option value="RBC">RBC</option><option value="PLATELETS">Platelets</option><option value="PLASMA">Plasma</option>
                    </select>
                 </div>
                 
                 <div className="flex gap-2">
                    <input type="number" placeholder="Units Needed" min="1" value={reqUnitsNeeded} onChange={e => setReqUnitsNeeded(e.target.value)} 
                       className="w-1/3 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none border border-gray-600" />
                    <select value={reqPriority} onChange={e => setReqPriority(e.target.value)} className="w-2/3 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none border border-gray-600">
                      <option value="ROUTINE">Routine (24 hrs)</option><option value="URGENT">Urgent (4 hrs)</option><option value="EMERGENCY">CRITICAL (30 min)</option>
                    </select>
                 </div>
               </div>

               <button onClick={handleCreateRequest} className="w-full bg-urgent hover:bg-orange-600 text-white font-bold py-3 rounded-lg transition-colors shadow-lg shadow-urgent/20">
                 Create Requisition Form
               </button>
            </div>


            <div className="bg-[#242436] p-6 rounded-xl border border-gray-700/50 shadow-md">
               <h3 className="text-white font-bold text-xl mb-2 flex items-center gap-2"><Droplet className="text-blue-400"/> Log Transfusion</h3>
               <p className="text-gray-400 text-sm mb-6">Record a completed transfusion securely against a Blood Unit.</p>

               <div className="space-y-3 mb-4">
                 <div className="flex gap-2">
                    <input type="number" placeholder="Blood Unit ID (Long)" value={transUnitId} onChange={e => setTransUnitId(e.target.value)} 
                       className="w-1/2 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500 border border-gray-600" />
                    <input type="number" placeholder="Request ID" value={transReqId} onChange={e => setTransReqId(e.target.value)} 
                       className="w-1/2 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500 border border-gray-600" />
                 </div>
                 <input type="text" placeholder="Patient Name" value={transPatient} onChange={e => setTransPatient(e.target.value)} 
                    className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none border border-gray-600" />
                 
                 <div className="flex gap-2">
                    <input type="text" placeholder="Hospital" value={transHospital} onChange={e => setTransHospital(e.target.value)} 
                       className="w-1/2 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none border border-gray-600" />
                    <input type="text" placeholder="Attending Staff" value={transBy} onChange={e => setTransBy(e.target.value)} 
                       className="w-1/2 bg-[#1A1A2E] text-white rounded-lg p-3 outline-none border border-gray-600" />
                 </div>

                 <div className="flex items-center gap-3 bg-[#1A1A2E] border border-gray-600 rounded-lg p-3">
                   <input type="checkbox" checked={transAdverse} onChange={e => setTransAdverse(e.target.checked)} className="w-5 h-5 accent-red-500 cursor-pointer"/>
                   <span className="text-white font-medium">Flag Adverse Reaction</span>
                 </div>
                 
                 {transAdverse && (
                   <textarea placeholder="Describe reaction details..." value={transRemarks} onChange={e => setTransRemarks(e.target.value)} 
                      className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none border border-red-500/50 min-h-[80px]" />
                 )}
               </div>

               <button onClick={handleLogTransfusion} className="w-full border-2 border-blue-500 text-blue-400 hover:bg-blue-500 hover:text-white font-bold py-3 rounded-lg transition-all duration-300">
                 Record Transfusion Log
               </button>
            </div>

            {/* My Requests Tracker */}
            <div className="col-span-full bg-[#1A1A2E] rounded-xl p-6 border border-gray-700/50 shadow-inner">
               <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><Activity className="text-urgent"/> My Requisition Tracker</h3>
               <p className="text-[10px] text-gray-500 mb-2 uppercase tracking-widest font-bold">Tracked Total: {myRequests.length}</p>
               <div className="space-y-3 max-h-64 overflow-y-auto pr-1">
                 {myRequests.length === 0 ? <p className="text-gray-400 italic text-sm">No requisitions submitted yet.</p> : myRequests.map(req => (
                   <div key={req.id} className="bg-[#242436] p-4 rounded-lg flex flex-col sm:flex-row justify-between items-start sm:items-center border border-gray-700/50 gap-2">
                     <div>
                       <p className="text-white font-semibold">{req.patientName} <span className="text-gray-400 text-xs font-normal">@ {req.requestingHospital}</span></p>
                       <p className="text-xs text-gray-400 mt-0.5">{req.unitsRequested}x {req.patientBloodGroup?.replace('_POS','+').replace('_NEG','-')} {req.componentType} · <span className={`font-bold ${req.priority === 'EMERGENCY' ? 'text-red-400' : req.priority === 'URGENT' ? 'text-orange-400' : 'text-green-400'}`}>{req.priority}</span></p>
                     </div>
                     <span className={`px-3 py-1 rounded-full text-[10px] font-bold tracking-widest border shrink-0 ${req.status === 'PENDING' ? 'bg-orange-500/10 text-orange-400 border-orange-500/30' : req.status === 'PROCESSING' ? 'bg-blue-500/10 text-blue-300 border-blue-500/30' : req.status === 'FULFILLED' ? 'bg-green-500/10 text-green-400 border-green-500/30' : 'bg-gray-500/10 text-gray-400 border-gray-500/30'}`}>{req.status}</span>
                   </div>
                 ))}
               </div>
            </div>

            {/* Available Inventory Preview */}
            <div className="col-span-full bg-[#1A1A2E] rounded-xl p-6 border border-gray-700/50 shadow-inner">
               <h3 className="text-white font-bold text-xl mb-4 flex items-center gap-2"><Droplet className="text-primary"/> Available Blood Bank Inventory</h3>
               <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                 {['O_NEG','O_POS','A_POS','A_NEG','B_POS','B_NEG','AB_POS','AB_NEG'].map(bg => {
                   const count = pubInventory.filter(u => u.bloodGroup === bg && u.status === 'AVAILABLE').length;
                   return (
                     <div key={bg} className={`rounded-xl p-4 text-center border ${count === 0 ? 'bg-red-900/20 border-red-500/20' : count < 3 ? 'bg-orange-900/20 border-orange-500/20' : 'bg-green-900/10 border-green-500/20'}`}>
                       <p className="text-2xl font-black text-white">{count}</p>
                       <p className={`text-xs font-bold tracking-widest mt-1 ${count === 0 ? 'text-red-400' : count < 3 ? 'text-orange-400' : 'text-green-400'}`}>{bg.replace('_POS','+').replace('_NEG','-')}</p>
                       <p className="text-[10px] text-gray-500 mt-0.5">{count === 0 ? '🔴 OUT' : count < 3 ? '⚠ LOW' : '✓ OK'}</p>
                     </div>
                   );
                 })}
               </div>
            </div>
          </div>
        );

      default:
        return <div className="text-white text-center py-20">Role interface not defined.</div>;
    }
  };

  return (
    <div className="min-h-screen bg-navy flex flex-col font-sans">
      <header className="bg-[#151522] border-b border-gray-800/80 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
             <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-primary to-orange-500 flex items-center justify-center shadow-lg shadow-primary/20">
                <Droplet size={18} className="text-white" />
             </div>
             <span className="font-bold text-xl text-white tracking-wide">Flow<span className="text-primary">Sync</span></span>
          </div>
          
          <div className="flex items-center gap-6">
            <div className="hidden sm:flex items-center gap-2">
              <div className="w-8 h-8 rounded-full bg-[#242436] border border-gray-600 flex items-center justify-center">
                <UserIcon size={16} className="text-gray-300"/>
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-semibold text-white leading-tight">{user?.email?.split('@')[0]}</span>
                <span className="text-[10px] text-primary uppercase font-bold tracking-wider">{user?.role}</span>
              </div>
            </div>
            <button onClick={logout} className="text-gray-400 hover:text-white transition-colors" title="Logout">
              <LogOut size={20} />
            </button>
          </div>
        </div>
      </header>

      <main className="flex-1 w-full max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
        <div className="mb-8 hidden sm:block">
           <h1 className="text-3xl font-bold text-white tracking-tight mb-1">
             {user?.role === 'DONOR' ? 'Donor Portal' : 
              user?.role === 'HOSPITAL_STAFF' ? 'Hospital Dispatch' : 
              user?.role === 'EMERGENCY_COORDINATOR' ? 'Emergency Command' : 'Admin Dashboard'}
           </h1>
           <p className="text-gray-400 text-sm">Real-time networking and inventory sync.</p>
        </div>
        {renderRoleDashboard()}
      </main>
    </div>
  );
};

export default Dashboard;
