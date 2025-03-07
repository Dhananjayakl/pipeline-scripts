import React, { Suspense } from "react";
import { useRoutes } from "react-router-dom";
import { Provider } from "react-redux";
import { HelmetProvider, Helmet } from "react-helmet-async";
import { store } from "./redux/store";
//import "./otel"; // This initializes OpenTelemetry
import "./i18n";
import routes from "./routes";
//import './otel';
import Loader from "./components/Loader";
//import { traceFunction } from "./otel";
import ThemeProvider from "./contexts/ThemeProvider";
import SidebarProvider from "./contexts/SidebarProvider";
import LayoutProvider from "./contexts/LayoutProvider";
import ChartJsDefaults from "./utils/ChartJsDefaults";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
//import MyComponent from './MyComponent'; // Adjust the path as necessary
import AuthProvider from "./contexts/JWTProvider";
// import {CollapseProvider} from './contexts/SectionProvider';
const App = () => {
  const content = useRoutes(routes);
  return (
    <HelmetProvider>
      <Helmet titleTemplate="%s | Progrec Apps" defaultTitle="Progrec Apps" />
      <Suspense fallback={<Loader />}>
        <Provider store={store}>
          <DndProvider backend={HTML5Backend}>
            <ThemeProvider>
              <SidebarProvider>
                <LayoutProvider>
                  <ChartJsDefaults />
                  {/* <CollapseProvider> */}
                  <AuthProvider>{content}</AuthProvider>
                  {/* </CollapseProvider> */}
                </LayoutProvider>
              </SidebarProvider>
            </ThemeProvider>
          </DndProvider>
        </Provider>
      </Suspense>
    </HelmetProvider>
  );
};

export default App;
