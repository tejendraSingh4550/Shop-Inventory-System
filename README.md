# 🏪 Shop Inventory Manager

A desktop inventory management app built with Java Swing — helps small shop owners track stock, get low-stock alerts, and monitor sales in real time.

## Problem it solves
Small shop owners often track inventory manually in notebooks, which makes it hard to know when items are running low or how much revenue is being generated. This app automates that with live alerts and running totals.

## Features
- **Add / Restock items** — adding an existing item name automatically merges quantity instead of duplicating it
- **Live inventory table** with color-coded rows:
  - 🟢 Green = healthy stock
  - 🟡 Yellow = low stock (5 or fewer units)
  - 🔴 Red = out of stock
- **Sell item** — select a row, enter quantity sold, stock and revenue update instantly
- **Live summary bar** — total items in stock, total inventory value, and total sales revenue
- **Low-stock alert banner** — highlights all items running low

## Tech Stack
- Java (Swing) — no external libraries required
- Nimbus Look & Feel (built into the JDK) for a modern UI

## How to Run

1. Make sure you have JDK 17+ installed:
   ```bash
   java -version
   ```

2. Compile and run:
   ```bash
   javac ShopInventory.java
   java ShopInventory
   ```

## Screenshots
_Add a screenshot of the app here after running it._

## License
MIT
