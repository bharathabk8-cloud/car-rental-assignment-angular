import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Option{category:string;durationDays:number;totalAmount:number}

@Component({
 selector:'app-root',
 standalone:true,
 imports:[CommonModule,FormsModule],
 templateUrl:'./app.component.html'
})
export class AppComponent{
 customerName='John Doe';
 startDate=''; endDate='';
 dailyMileage=100; licenseYears=5;
 options:Option[]=[]; message=''; reservation:any=null;

 async getOptions(){
   const q=`/api/options?startDate=${this.startDate}&endDate=${this.endDate}&dailyMileage=${this.dailyMileage}&licenseYears=${this.licenseYears}`;
   const r=await fetch(q); const d=await r.json();
   if(!r.ok){this.message=d.message;return;}
   this.options=d;
 }
 async reserve(category:string){
   const r=await fetch('/api/reservations',{method:'POST',headers:{'Content-Type':'application/json'},
   body:JSON.stringify({customerName:this.customerName,category,startDate:this.startDate,endDate:this.endDate,dailyMileage:this.dailyMileage,licenseYears:this.licenseYears})});
   const d=await r.json(); if(!r.ok){this.message=d.message;return;}
   this.reservation=d; this.message='Reservation Created';
 }
 async cancel(){
   const r=await fetch(`/api/reservations/${this.reservation.reservationId}`,{method:'DELETE'});
   this.reservation=await r.json(); this.message='Reservation Cancelled';
 }
}
