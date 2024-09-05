﻿using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class ChecklistBusiness : IChecklistBusiness
    {
        private readonly IChecklistData data;

        public ChecklistBusiness(IChecklistData data)
        {
            this.data = data; 
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<ChecklistDto>> GetAll()
        {
            IEnumerable<Checklist> checklists = await data.GetAll();
            var checklistDtos = checklists.Select(checklist => new ChecklistDto
            {
                Id = checklist.Id,
                Calification_total = checklist.Calification_total,
                Code = checklist.Code,
                State = checklist.State
            });

            return checklistDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<ChecklistDto> GetById(int id)
        {
            Checklist checklist = await data.GetById(id);
            ChecklistDto dto = new ChecklistDto();
            dto.Id = checklist.Id;
            dto.Calification_total = checklist.Calification_total;
            dto.Code = checklist.Code;
            dto.State = checklist.State;
            return dto;
        }

        public Checklist mapearDatos(Checklist checklist, ChecklistDto entity)
        {
            checklist.Id = entity.Id;
            checklist.Calification_total = entity.Calification_total;
            checklist.Code = entity.Code;
            checklist.State = entity.State;
            return checklist;
        }

        public async Task<Checklist> Save(ChecklistDto entity)
        {
            Checklist checklist = new Checklist();
            checklist = mapearDatos(checklist, entity);
            checklist.Created_at = DateTime.Now;
            checklist.Updated_at = null;
            checklist.Deleted_at = null;

            return await data.Save(checklist);
        }

        public async Task Update(ChecklistDto entity)
        {
            Checklist checklist = await data.GetById(entity.Id);
            if (checklist == null)
            {
                throw new Exception("Registro no encontrado");
            }
            checklist = mapearDatos(checklist, entity);
            checklist.Updated_at = DateTime.Now;

            await data.Update(checklist);
        }
    }
}