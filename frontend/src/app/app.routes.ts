import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { RecognitionsComponent } from './features/recognitions/recognitions.component';
import { BadgesComponent } from './features/badges/badges.component';
import { DigestComponent } from './features/digest/digest.component';
import { LoginComponent } from './features/login/login.component';
import { SignupComponent } from './features/signup/signup.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignupComponent },
  { path: 'signup', redirectTo: 'register', pathMatch: 'full' },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'recognitions', component: RecognitionsComponent, canActivate: [AuthGuard] },
  { path: 'badges', component: BadgesComponent, canActivate: [AuthGuard] },
  { path: 'digest', component: DigestComponent, canActivate: [AuthGuard] }
];
