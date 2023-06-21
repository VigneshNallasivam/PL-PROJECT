package com.intelizign.pl.service;


import java.time.LocalDate;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.intelizign.pl.model.ProjectModel;
import com.intelizign.pl.model.ResourceModel;
import com.intelizign.pl.repositories.ProjectRepository;

@Service
public class SchedulerService 
{
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private EmailService emailService;

	public final Logger LOGGER = LogManager.getLogger(SchedulerService.class);

	@Scheduled(cron = "0 0 10 * * *")
	public void projectDateInfo()
	{
		LocalDate currentDate = LocalDate.now();
		LocalDate endDate = currentDate.plusDays(2);
		List<ProjectModel> projectData = projectRepository.findByActiveAndEndDate(true,endDate);
		if(projectData!=null)
		{
			for(ProjectModel projectDetails : projectData)
			{
				List<ResourceModel> resources = projectDetails.getProject_resources();
				if(resources!=null && !resources.isEmpty())
				{
					for(ResourceModel resourceModel : resources) 
					{
						emailService.sendEmail(resourceModel.getEmail(),resourceModel);
					}
				}

			}

		}
	}
}


