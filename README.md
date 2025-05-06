# 💸 Budget & Expense Tracker (Java Console App)

A command-line Java application that helps users efficiently manage their personal finances with features like budget creation, expense tracking, undo deletion, search, sort, analytics graphs, and persistent data storage. Built with core Java, custom data structures, and a file-based datastore — no external databases or frameworks.

---

## 🚀 Features

- 🔐 **User Registration & Login**
- 📊 **Dashboard**: Total limit, spent, remaining — with visual graph
- 📁 **Manage Budgets**: Create, view, delete, search, sort
- 🧾 **Manage Expenses**: Add, edit, delete under each budget
- ↩️ **Undo Last Deletion**: Recover deleted budgets/expenses
- 🕒 **Recent Actions Tracker**: Logs latest activities
- 🔍 **Search Budgets by Name** (Binary Search Tree)
- 📈 **Sort Budgets by Limit** (QuickSort)
- 💾 **Persistent Storage** via `data.json`
- 🧠 **Custom Data Structures**: Stack, Queue, LinkedList
- 📉 **Analytics**: Graphical CLI charts for budget usage
- 💬 **Clean CLI Interface** with box designs and input handling

---

## 🧠 Tech Stack & Structure

- **Language**: Java
- **Data Structures**:
  - Stack → Undo deletion
  - Queue → Recent action log
  - LinkedList → Dynamic budget/expense storage
  - BinarySearchTree → Search budgets
  - Array + QuickSort → Sort budgets
- **Storage**: Local file-based (JSON)
- **Utilities**: ConsoleUtils for CLI graphs and box UI
- **Package Structure**:
