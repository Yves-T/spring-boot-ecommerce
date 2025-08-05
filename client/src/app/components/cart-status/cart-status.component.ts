import { AsyncPipe, CurrencyPipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Observable, of } from 'rxjs';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart-status',
  imports: [CurrencyPipe, AsyncPipe, RouterModule],
  templateUrl: './cart-status.component.html',
  styleUrl: './cart-status.component.css',
})
export class CartStatusComponent implements OnInit {
  totalPrice$: Observable<number> = of(0);
  totalQuantity$: Observable<number> = of(0);

  cartService = inject(CartService);

  ngOnInit(): void {
    this.updateCartStatus();
  }

  updateCartStatus() {
    this.totalPrice$ = this.cartService.totalPrice.asObservable();
    this.totalQuantity$ = this.cartService.totalQuantity.asObservable();
  }
}
