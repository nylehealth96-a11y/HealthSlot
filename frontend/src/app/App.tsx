import { useState } from 'react'
import { PatientWorkspace } from '../features/patient/PatientWorkspace'
import { DoctorWorkspace } from '../features/doctor/DoctorWorkspace'
import { ReceptionWorkspace } from '../features/reception/ReceptionWorkspace'

type Role = 'patient' | 'doctor' | 'reception'
export function App() {
  const [role, setRole] = useState<Role>('patient')
  return <main><h1>HealthSlot</h1><nav aria-label="Workspace"><button onClick={() => setRole('patient')} aria-pressed={role === 'patient'}>Patient</button><button onClick={() => setRole('doctor')} aria-pressed={role === 'doctor'}>Doctor</button><button onClick={() => setRole('reception')} aria-pressed={role === 'reception'}>Reception/Admin</button></nav>{role === 'patient' && <PatientWorkspace />}{role === 'doctor' && <DoctorWorkspace />}{role === 'reception' && <ReceptionWorkspace />}</main>
}
