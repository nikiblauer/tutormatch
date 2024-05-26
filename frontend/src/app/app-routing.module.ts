import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent, LoginMode} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {RegisterComponent} from "./components/register/register.component";
import {VerifyComponent} from "./components/register/verify/verify.component";
import {MatchComponent} from "./components/match/match.component";
import {AdminComponent} from './components/admin/admin.component';
import {DashboardComponent} from './components/admin/dashboard/dashboard.component';
import {StudentsComponent} from './components/admin/students/students.component';
import {UserProfileComponent} from './components/user-profile/user-profile.component';
import {SubjectComponent} from "./components/admin/subjects/subjects.component";
import {ChatComponent} from "./components/chat/chat.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent, data: {mode: LoginMode.user} },
  {
    path: 'register', children: [
      {path: '', component: RegisterComponent},
      {path: 'verify/:token', component: VerifyComponent}
    ]
  },
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'matches', component: MatchComponent},
  {path: 'myprofile', component: UserProfileComponent},
  {path: 'chat', component: ChatComponent},
  {
    path: 'admin', component: AdminComponent, children: [
      {path: 'login', component: LoginComponent, data: {mode: LoginMode.admin}},
      {path: 'dashboard', component: DashboardComponent},
      {path: 'students', component: StudentsComponent},
      {path: 'subjects', component: SubjectComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
