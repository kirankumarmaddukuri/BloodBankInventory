import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { ShieldAlert, Droplet } from 'lucide-react';

const LoginPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState('DONOR');
  const [bloodGroup, setBloodGroup] = useState('O_POS');
  const [dateOfBirth, setDateOfBirth] = useState('');
  const [error, setError] = useState('');
  
  const { login, register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (isLogin) {
        await login(email, password);
      } else {
        await register({ email, password, name, phone, role, bloodGroup: role === 'DONOR' ? bloodGroup : null, dateOfBirth: role === 'DONOR' ? dateOfBirth : null });
      }
      navigate('/dashboard');
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-navy p-4">
      <div className="bg-[#242436] rounded-2xl shadow-xl w-full max-w-md p-8 border border-white/5">
        <div className="flex flex-col items-center mb-8">
          <div className="bg-primary/20 p-3 rounded-full mb-3 border border-primary/30">
            <Droplet className="w-8 h-8 text-primary" />
          </div>
          <h2 className="text-2xl font-bold text-white tracking-wide">
            {isLogin ? 'Welcome Back' : 'Create Account'}
          </h2>
          <p className="text-gray-400 text-sm mt-1">Regional Blood Bank Network</p>
        </div>

        {error && (
          <div className="mb-4 p-3 bg-red-500/20 border border-red-500/50 rounded-lg flex items-center gap-2 text-red-200 text-sm">
            <ShieldAlert size={16} /> <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {!isLogin && (
            <>
              <div>
                <label className="block text-gray-400 text-xs font-semibold mb-1 uppercase tracking-wider">Full Name</label>
                <input type="text" required value={name} onChange={e => setName(e.target.value)} 
                  className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-700/50 transition-all"/>
              </div>
              <div>
                <label className="block text-gray-400 text-xs font-semibold mb-1 uppercase tracking-wider">Phone</label>
                <input type="text" required value={phone} onChange={e => setPhone(e.target.value)} 
                  className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-700/50 transition-all"/>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-gray-400 text-xs font-semibold mb-1 uppercase tracking-wider">Role</label>
                  <select value={role} onChange={e => setRole(e.target.value)} 
                    className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-700/50 transition-all appearance-none cursor-pointer">
                    <option value="DONOR">Donor</option>
                    <option value="HOSPITAL_STAFF">Hospital</option>
                    <option value="BLOOD_BANK_ADMIN">Blood Bank</option>
                    <option value="EMERGENCY_COORDINATOR">Dispatcher</option>
                    <option value="ADMIN">System Admin</option>
                  </select>
                </div>
                {role === 'DONOR' && (
                  <>
                    <div>
                      <label className="block text-gray-400 text-xs font-semibold mb-1 uppercase tracking-wider">Date of Birth</label>
                      <input type="date" required value={dateOfBirth} onChange={e => setDateOfBirth(e.target.value)} 
                        className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-700/50 transition-all [color-scheme:dark]"/>
                    </div>
                    <div>
                      <label className="block text-gray-400 text-xs font-semibold mb-1 uppercase tracking-wider">Blood Group</label>
                      <select value={bloodGroup} onChange={e => setBloodGroup(e.target.value)} 
                        className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-700/50 transition-all appearance-none cursor-pointer">
                        <option value="O_POS">O+</option>
                        <option value="A_POS">A+</option>
                        <option value="B_POS">B+</option>
                        <option value="AB_POS">AB+</option>
                        <option value="O_NEG">O-</option>
                        <option value="A_NEG">A-</option>
                        <option value="B_NEG">B-</option>
                        <option value="AB_NEG">AB-</option>
                      </select>
                    </div>
                  </>
                )}
              </div>
            </>
          )}

          <div>
            <label className="block text-gray-400 text-xs font-semibold mb-1 uppercase tracking-wider">Email Address</label>
            <input type="email" required value={email} onChange={e => setEmail(e.target.value)} 
              className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-700/50 transition-all"/>
          </div>

          <div>
            <label className="block text-gray-400 text-xs font-semibold mb-1 uppercase tracking-wider">Password</label>
            <input type="password" required value={password} onChange={e => setPassword(e.target.value)} 
              className="w-full bg-[#1A1A2E] text-white rounded-lg p-3 outline-none focus:ring-2 focus:ring-primary border border-gray-700/50 transition-all"/>
          </div>

          <button type="submit" 
            className="w-full bg-primary hover:bg-primaryHover text-white font-bold py-3 rounded-lg shadow-lg shadow-primary/30 transition-all active:scale-[0.98] mt-2">
            {isLogin ? 'Sign In' : 'Register Account'}
          </button>
        </form>

        <p className="text-center text-sm text-gray-400 mt-6 font-medium">
          {isLogin ? "Don't have an account?" : "Already registered?"}
          <button onClick={() => setIsLogin(!isLogin)} className="ml-1 text-primary hover:text-white transition-colors">
            {isLogin ? 'Sign up' : 'Login'}
          </button>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
