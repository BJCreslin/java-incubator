import React from 'react';
import {connect} from "react-redux";
import Preloader from "../../common/preloader/Preloader";

import {
    deleteProjectThunkCreator,
    getPaginationProjectsThunkCreator,
    setCurrentPage,
    setFirstPage,
    setLastPage,
    setNextPage,
    setPreviousPage,
    setToggleFetching,
    updateProjectThunkCreator
} from "../../../redux/projects-reducer";
import {compose} from "redux";
import {withAuthRedirect} from "../../../HOC/withAuthRedirect";
import ShowProjects from "./ShowProjects";

class ProjectsContainer extends React.Component {

    projectsUpdate() {
        this.props.getPaginationProjectsThunkCreator(this.props.currentPage, this.props.numberForPage);
    }

    componentDidMount() {
        this.projectsUpdate();
    };

    componentDidUpdate(prevProps, prevState, snapshot) {
        debugger
        if (this.props.currentPage !== prevProps.currentPage) {
            this.projectsUpdate();
        }
        if (this.props.totalCount !== prevProps.totalCount) {
            this.projectsUpdate();
        }
    };

    render = () => {
        return (
            <>
                {this.props.isFetching ? <Preloader/> : null}
                <ShowProjects projects={this.props.projects}
                              currentPage={this.props.currentPage}
                              setCurrentPage={this.props.setCurrentPage}
                              setNextPage={this.props.setNextPage}
                              setPreviousPage={this.props.setPreviousPage}
                              isFetching={this.props.isFetching}
                              setFirstPage={this.props.setFirstPage}
                              setLastPage={this.props.setLastPage}
                              numberForPage={this.props.numberForPage}
                              getPaginationProjectsThunkCreator={this.props.getPaginationProjectsThunkCreator}
                              updateProjectThunkCreator={this.props.updateProjectThunkCreator}
                              deleteProjectThunkCreator={this.props.deleteProjectThunkCreator}
                />
            </>
        )
    };
}

const mapStateToProps = (state) => {
    return {
        projects: state.projectsPage.projects,
        totalCount: state.projectsPage.totalCount,
        isFetching: state.projectsPage.isFetching,
        currentPage: state.projectsPage.currentPage,
        numberForPage: state.projectsPage.numberForPage
    }
};

const mapDispatchToProps = {
    setToggleFetching,
    getPaginationProjectsThunkCreator,
    updateProjectThunkCreator,
    setCurrentPage,
    setFirstPage,
    setLastPage,
    setNextPage,
    setPreviousPage,
    deleteProjectThunkCreator
};


export default compose(
    withAuthRedirect,
    connect(mapStateToProps, mapDispatchToProps))
(ProjectsContainer);
